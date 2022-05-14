# Kernel Development Part 4: C Compiler
Assembly is all well and good, but it's a pain to write an OS using it. Ideally we want to write it in C. To do this, we need a C compiler, which will turn C into Assembly and then into machine code. We will be compiling the GCC compiler. We can't use GCC directly, because it's compiled for linux. What we need is a [_cross compiler_](https://wiki.osdev.org/GCC_Cross-Compiler). That page has a full guide, and the video goes through the steps, so I won't replicate it here, but bear in mind it takes a **long time**, like 1 hour plus.

Next we need to write a build script that will make working with C directly easier by setting the path variable. Create a `build.sh`, and add the following, and make it executable with `chmod +x build.sh`.

```bash
#/bin/bash
export PREFIX="$HOME/opt/cross"
export TARGET=i686-elf
export PATH="$PREFIX/bin:$PATH"

make all
```

The rest of this section is going to revolve around breaking out our kernel into it's own file. We'll end up with a `boot.asm` and `kernel.asm`, and then we will combine the output binaries of these two into an `os.bin`, which will be our final image, with the bootloader being the first sector and the kernel being the remainder, with the bootloader loading the kernel into memory.

```ld
ENTRY(_start)
OUTPUT_FORMAT(binary)
SECTIONS
{
  . = 1M;
  .text : 
  {
    *(.text)
  }

  .rodata :
  {
    *(.rodata)
  }

  .data :
  {
    *(.data)
  }

  .bss :
  {
    *(COMMON)
    *(.bss)
  }
}
```

Don't worry too much about what all this does for now, but in summary we are saying that our sections (and therefore our kernel) will start at memory address `1M`: address at 1Mb, 1,048,576 in decimal or 100,000 hex.

Next we modify our makefile so it builds the whole project:

```make
FILES = ./build/kernel.asm.o

all: ./bin/boot.bin ./bin/kernel.bin
	rm -f ./bin/os.bin
	dd if=./bin/boot.bin >> ./bin/os.bin
	dd if=./bin/kernel.bin >> ./bin/os.bin
	dd if=/dev/zero bs=512 count=100 >> ./bin/os.bin

./bin/kernel.bin: $(FILES)
	i686-elf-ld -g -relocatable $(FILES) -o ./build/kernelfull.o
	i686-elf-gcc -T ./src/linker.ld -o ./bin/kernel.bin -ffreestanding -O0 -nostdlib ./build/kernelfull.o

./bin/boot.bin: ./src/boot/boot.asm
	nasm -f bin ./src/boot/boot.asm -o ./bin/boot.bin

./build/kernel.asm.o: ./src/kernel.asm
	nasm -f elf -g ./src/kernel.asm -o ./build/kernel.asm.o

clean:
	rm -f ./bin/boot.bin
	rm -f ./bin/kernel.bin
	rm -f $(FILES)
	rm -f ./build/kernelfull.o
```

Again, the details of how make works are not in scope of this course, but in summary we are building the kernel asm into an `asm.o` (an "object file"), building that into a `kernel.bin` using gcc and our linker, building `boot.bin` and finally combining those two bins into `os.bin`. Note in particular the `-g` flag that is used a couple of times. This will allow us to use debugging symbols later on. Note also the padding of nulls we add to the end of `os.bin`: out to 100 sectors of 512 bytes. This will allow our kernel to grow without us having to change anything.

Back in `kernel.asm`, there is a reference to `DATA_SEG`, but this is no longer defined. So add it in. Recall that the addresses correspond to the entries in the Global Descriptor Table. The `global _start` corresponds to our `ENTRY(_start)` in the linker:

```asm
[BITS 32]
global _start
CODE_SEG equ 0x08
DATA_SEG equ 0x10

_start:
    mov ax, DATA_SEG
    ; snip
```

## Next we need to write a driver and load the Kernel into memory
We need to load the Kernel into memory. Given we are now in protected mode, we can't access the BIOS function like we did earlier, so we need to write a quick disk driver, which will tell our bootloader how to interact with the ATA Protocol disk controller by spitting data onto the processor bus. Add the following to your `boot.asm`. Don't worry too much about the detail beyond the comments

```asm
[BITS 32]
load32:
    mov eax, 1         ; LBA: starting sector we want to load from disk, 1
    mov ecx, 100       ; sector we want to load to, because we wrote 100 sectors of nulls in make
    mov edi, 0x0100000 ; 1Mb in hex, where we want to load our Kernel into RAM 
    call ata_lba_read  ; load the kernel to memory
    jmp CODE_SEG:0x0100000 ; jump to the memory address to where we loaded the kernel

; this is like a 'dummy disk driver' to load our kernel. We'll write a proper one in C later
ata_lba_read:
    mov ebx, eax ; backup LBA
    ; Send highest 8 bits of LBA to hard disk controller
    shr eax, 24   ; shift eax 24 bits to the right
    or eax, 0xE0  ; selects the master drive
    mov dx, 0x1F6 ; port on the Hard Disk
    out dx, al

    ; Send total sectors
    mov eax, ecx
    mov dx, 0x1F2
    out dx, al

    ; send some more bits to the disk controller
    mov eax, ebx ; restore backup LBA
    mov dx, 0x1F3
    out dx, al

    mov eax, ebx
    mov dx, 0x1F4
    shr eax, 8
    out dx, al

    ; send upper 16 bits of LBA
    mov eax, ebx
    mov dx, 0x1F5
    shr eax, 16
    out dx, al

    mov dx, 0x1f7
    mov al, 0x20
    out dx, al

    ; read sectors
.next_sector:
    push ecx

    ;checking if we need to read
.try_again:
    mov dx, 0x1f7
    in al, dx
    test al, 8
    jz .try_again

    ; read 256 words at a time
    mov ecx, 256
    mov dx, 0x1F0
    rep insw ; input word from port per DX into memloc ES:EDI - we've set EDI to 1Mb
    pop ecx
    loop .next_sector
    ; end of reading sectors into memory
    ret
```

## Alignment and solving alignment issues with C code and ASM.

## Bits, byte, binary and hex

* A bit is a single binary digit 0 or 1.
* 2 binary digits can represent 4 unique values: `00`, `01`, `10`, `11`
* n binary digits can represent 2^n unique values.
* 4 binary digits can represent 2^4 = 16 unique values
* 8 binary digits can represent 2^8 = 256 values.
* 8 bits, represented by an 8 digit binary, is a byte, which can take one of 256 values.
* A hex digit can take the values `0-F`, or 16 possible values
* A single hex digit is equivalent to 4 binary digits
* Two hex digits (`00` to `FF`) represent 16^2 = 256 values. Therefore 2 hex digits represents one byte.
* 4 hex digits (`0000` to `FFFF`) represent 2 bytes, or 16 bits.
* Each memory location can hold a single byte, or 2 digit hex number.
