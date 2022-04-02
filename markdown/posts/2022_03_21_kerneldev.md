% Kernel Development

This is based on a [course](https://dragonzap.com/course/developing-a-multithreaded-kernel-from-scratch?coupon=YOUTUBEKERNEL2022) which guides the reader through the development of a full OS kernel, from a Hello World Bootloader to a multitasking kernel with a FAT16 file system. The first 11 lessons are available for free on [YouTube](https://www.youtube.com/watch?v=HNIg3TXfdX8&list=PLrGN1Qi7t67V-9uXzj4VSQCffntfvn42v)

## What is memory?

<div class="tufte-section">
<div class="main-text">

Memory is a piece of hardware that allows computers to store information. Programs can read and write to _Random Access Memory_ (RAM). It's only used for temporary storage, such as for variable storage of the programs you write. RAM is wiped when you shut down the computer. _Read Only Memory_ (ROM) does _not_ vanish when you shut down the computer. But as the name suggests, you can't write to it. In a home PC, the BIOS program is stored in ROM.

Memory is generally accessed in a linear fashion. The data is stored in order. The way your processor accesses memory abstracts this.

</div>
<div class="sidenotes">
</div>
</div>

## The Boot Process

<div class="tufte-section">
<div class="main-text">

The boot process has three steps:

1. The BIOS<sup>1</sup> program is executed from ROM/BIOS Chip
2. The BIOS loads the _Bootloader_ into memory address `0x7C00`
3. The Bootloader loads the Kernel

When the computer is switched on, the CPU will read from the BIOS ROM and start executing instructions it finds there. The BIOS usually loads itself into RAM for performance reasons and will continue to execute from there. The BIOS also identifies and initializes the computer's hardware, such as disk drivers.

The final thing the BIOS does is try to find a Bootloader. It searches all available storage mediums - USB drives, hard disks - for the magic boot signature `0x55AA`. It will look in the last bytes of the first sector<sup>2</sup>, and if the signature is found, it will load that sector into address `0x7C00`, and the CPU will start to execute from that address.

When a computer first boots, it does so in _"Real Mode"_. This is a very limited 'compatibility' mode, with access to only 1Mb of memory, and it can only execute 16 bit code. The Bootloader is a small program whose job is to put the computer into _"Protected Mode"_, which allows 32 bit code and access to 4Gb of memory, and then to load the kernel of an operating system.

The BIOS contains routines that the bootloader uses to boot the kernel. The interfaces of BIOS routines are generic and standardized across manufacturers.

</div>
<div class="sidenotes">
<sup>1</sup> Basic Input Output System

![booting](../../images/2021_03_21_kerneldev/booting.png)

<sup>2</sup> A sector is a block of storage. For example, a hard disk is made up of 512 byte sectors. The BIOS will look in the byte addresses 511 and 512 of the sector.
</div>
</div>

## Getting setup for Kernel development

<div class="tufte-section">
<div class="main-text">
All development will be done on Ubuntu Linux. First, make sure your repositories are up to date. Then, install nasm<sup>1</sup>. Finally, we will use the QEmu emulator to run our bootloader and kernel. Test that it runs using the below commands. A new window will pop up, but since there are no disks attached it won't be able to boot.

```
sudo apt update
sudo apt install nasm

sudo apt install qemu-system-x86
qemu-system-x86_64
```

</div>
<div class="sidenotes">
<sup>1</sup> NASM is the "Netwide Assembler", and assembler for the x86 CPU architecture, compatible with nearly every modern platform. https://nasm.us
</div>
</div>

## Assembly language

<div class="tufte-section">
<div class="main-text">

This is a short refresher on what assembly language is. Or, for me, basically an introduction to it, since I've never written it before.

Your processor has an _instruction set_, or machine codes. Assembly language gets passed through an _assembler_, and machine codes your processor understands come out the other side. Use [Emu8086](https://emu8086-microprocessor-emulator.en.softonic.com/) to easily test assembly programs.<sup>2</sup>

Write the following program in your emulator:

```assembly
mov ah, 0eh
mov al, 'A'
int 10h
```

This is a program that outputs 'A' to the screen. `mov X Y` moves data Y to register<sup>3</sup> X. `ah` ("Accumulator High") and `al` ("Accumulator Low") are registers storing one byte. So here we move `0eh`<sup>4</sup>  into `ah`, and the character 'A' (65) into `al`. `int X` means _"interrupt with code X"_. The code `10h` outputs things to the screen<sup>5</sup>.

So we have a program that outputs a _character_ to a screen. Now we need to output multiple characters. Here is our next program.

```assembly
jmp main

message: db 'Hello World!', 0

print:
    mov ah, 0eh
._loop:
    lodsb
    cmp al, 0
    je .done
    int 10h
    jmp ._loop
.done:
    ret

main:
    mov si, message
    call print
```

The first thing to point out is that assembly always executes top-to-bottom unless you specifically tell it to jump to somewhere else. This is done with _labels_. These are the words that end with colons, like `main:`. For example `print` is a subroutine, and when it is called with `call print`, the program will start executing at this point.

The line `message: db 'Hello World!', 0`, puts a block of data representing the bytes 'Hello World!' at the starting memory location (`db` mean data bytes I think). The zero at the end is the 'null' terminator. We have to put `jmp main` above that, otherwise the processor will try to execute the data, which it will think are instructions.

Main has been changed to move register `si` ("Source index", used as a data pointer) to the address of our message. The it calls `print`. The `print` subroutine is an elaboration on the "print character" code. The first instructions are the `lodsb` ("load string byte"), which loads the character at `si` into `al`, and increments `si`, moving it to the address of the next character. `cmp` "compares" the value in `al` to 0, and if it is 0 (meaning we are at the end of the string), it jumps to done and returns. Otherwise it jumps back to the loop label



</div>
<div class="sidenotes">
<sup>2</sup> For linux users, it works fine with Wine.

<sup>3</sup> A register is a storage location in the processor.

<sup>4</sup> `0e` in hex, 14 in decimal

<sup>5</sup> Specifically, 10h is a _BIOS routine_: A function that is defined in the BIOs, and can only be used in compatibility mode.

</div>
</div>

## A Hello World Bootloader

<div class="tufte-section">
<div class="main-text">

Next we will turn our "hello world" program into a bootloader. That is, the program will be loaded by the BIOS and, when we boot the machine, it will show "Hello World!" on the screen.

Create a new folder / project _PeachOS_. Make a new file _boot.asm_, and add the following code:

```assembly
ORG 0x7c00
BITS 16

start:
    mov si, message
    call print
    jmp $

print:
    mov bx, 0
.loop:
    lodsb
    cmp al, 0
    je .done
    call print_char
    jmp .loop
.done:
    ret

print_char:
    mov ah, 0eh
    int 0x10
    ret

message: db 'Hello World!', 0

times 510-($ - $$) db 0 ; Pad to 510th byte
dw 0xAA55 ; dw=define word. Puts bootloader signal
```

This is very similar to our non-bootloader code. There are a couple of organizational changes (`print_char` has been extracted into a subroutine, things have been moved around a bit), and a couple that are required to make this function as a bootloader. `ORG 0x7C00`  sets the 'origin' or 'starting location' where your program is loaded into memory. Bootloaders are always loaded to `0x7C00`. `BITS 16` tells the processor that it should interpret the program in 16bit mode ("Real Mode", or "Compatibility Mode"). The last two lines pad out the program with zeros, and then put the value `0x55AA` (the 'signal' that this is a bootloader) to the last 2 bytes of the sector.<sup>1</sup>

Assemble your bootloader and boot it with

```bash
nasm -f bin ./boot.asm -o ./boot.bin
ndisasm boot.bin #look at the machine code
qemu-system-x86_64 -hda ./boot.bin
```

</div>
<div class="sidenotes">
<sup>1</sup> Note that we say `0xAA55` in the code. This is because bytes are loaded 'backwards', with `55h` being loaded first.
</div>
</div>

## Segmentation Memory Model

<div class="tufte-section">
<div class="main-text">

We have seen that the pointer registers in the processor are 2 bytes. That means your instruction pointer for example, which is 2 bytes, can point to memory locations _(addresses)_ between bytes number 0 and (2^16) 65,535. However we've seen that in real mode, the processor has access to 1Mb of memory, or 1,048,576 bytes. How can we get our pointers to point to values above 65,535?

The answer is the _Segmentation Memory Model_. Memory is accessed by the combination of a _segment_ and an _offset_. This is what the _segment registers_ are for. There are 4 in the 8086: Code segment `cs`, Data segment `ds`, Extra segment `es` and Stack segment `ss`. The segment and offset can be combined to calculate the _absolute offset_ (the actual memory location in RAM) by multiplying the segment by 16 (A left shift in hex) and adding the offset<sup>1</sup>. For example, if your segment is `0x7C0` and your offset (instruction pointer) and origin are both zero. The absolute memory address your program will start executing at is `0x7C0 * 16 = 0x7C00`. If your offset is `0xFF`, the absolute address will be `0x7CFF`. If segment is `0xF000` and offset is `0xFFFF`, the absolute memory address is `FFFFF`, or 1,048,575. This is how you address a megabyte of memory using two 16bit registers. Note that this model means that you can get to an address in multiple ways. For example, if your segment is `0x7CF` and offset is `0x0F`, the absolute address is also `0x7CFF`.

Different instructions in the processor's instruction set use different combinations of registers to determine which absolute address to look at. For example, `lodsb` which we've already seen uses the data segment register and the source index register (shorthanded to `ds:si`).

Segment registers can be used in source using the following convention:

```assembly
mov byte al, [es:32]
```

This will move the byte located in `es:32` into `al`.

One thing we need to do with our bootloader is to make sure all the segment registered are initialized to the values we want. The BIOS and interrupts can sometimes mess with these. Change the origin to 0. Give a `jmp` instruction to `0x7c0`, which changes the instruction pointer. Change data and extra segments to `0x7c0`. Change the stack segment to `0x00` and the stack _pointer_ to `0x7c00`.

```assembly
ORG 0
BITS 16

jmp 0x7c0:start

start:
    cli ; clear interrupts
    mov ax, 0x7C0
    mov ds, ax
    mov es, ax
    mov ax, 0x00
    mov ss, ax
    mov sp, 0x7c00
    sti ; enables interrupts again
    mov si, message
    call print
```

To reiterate what our code is now doing:

1. The CPU (running the BIOS) finds the value `0x55AA` in our binary 'hard drive'
2. It loads that sector (0-512 bytes) into RAM starting at address `0x7c00`, and starts executing
3. The first instruction jumps to `0x7c0:start`<sup>2</sup>

</div>
<div class="sidenotes">

<sup>1</sup> Note that `ORG` or origin is also factored in.

<sup>2</sup> Frankly I don't get why - isn't this redundant?

</div>
</div>

## Interrupts and the Interrupt Vector Table

<div class="tufte-section">
<div class="main-text">

Interrupts are like subroutines that you call through 'interrupt numbers' rather than memory addresses. There are interrupts in the BIOS - we saw `10h 0eh` prints a character to the screen - or they can be set up by the programmer. Interrupts are special because they halt the processor, save the current state (meaning what? the registers?) to the stack, execute the interrupt, then restore the pre-interrupt state. 

The code for these interrupts are stored in RAM. The locations of the code are stored in the _interrupt vector table_<sup>1</sup>, which starts right at the beginning of RAM at address `0x00`. There are 256 entries in numerical order, `0x00` to `0xFF`, and each contains a 4 bytes: a 2 byte OFFSET and a 2 byte SEGMENT. This means you can calculate the location in the IVT of any interrupt code with `code * 0x04`. Interrupt `0x13` is at `0x46`.

The processor can throw exceptions with interrupts. For example, if you try to divide by zero in an Intel processor, it will call interrupt 0.<sup>2</sup>

In the following code, we _replace_ interrupt 0 with our own subroutine, by replacing the entries in the IVT to point to the subroutine in memory. Running our bootloader in this state will cause the screen to show `AHello World!A` - the first `A` comes from our manual call to the `int 0`, and the second comes from our attempt to divide by zero, which causes the processor to run interrupt 0.

```assembly
start:
    ; snip
    mov word[ss:0x00], handle_zero ; Set offset to handle_word address
    mov word[ss:0x02], 0x07C0      ; set segment to 0x07c0 

    int 0 ; call interupt 0

    mov si, message
    call print

    mov ax, 0x00
    div ax ; try to divide by 0

    jmp $

handle_zero:
    mov ah, 0eh
    mov al, 'A'
    mov bx, 0x00
    int 0x10
    iret

; snip
```


</div>
<div class="sidenotes">

![Interrupt Vector Table](../../images/2021_03_21_kerneldev/IVT.png)

<sup>1</sup> All of this only applies to Real Mode. In Protected Mode, an "Interrupt Descriptor Table" is used instead. More on this later

<sup>2</sup> wiki.osdev.org/exceptions is a great resource for learning more

</div>
</div>

## Reading from disk

<div class="tufte-section">
<div class="main-text">

Next we will see how we can read data from a hard disk. Note that we're not talking about accessing _files_. Files and the file system are implemented in the Kernel. Or to be more specific, the disk is 'formatted' with a particular file system data structure (FAT, EXT4 etc.), and the kernel has drivers which are able to interpret that data structure as files. As far as we are concerned, the disk consists of blocks of data called _sectors_<sup>1</sup>. A sector consists of 512 contiguous bytes. These sectors are read and written in sector blocks, not by accessing individual bytes.

The old way of addressing disk sectors is the _Cylinder Head Sector_ (CHS) system. This is from when disks were spinning magnetic plates arranged in cylinders. You need to specify the cylinder, head, sector and track you want to read from. This was pretty complicated, and it is no longer really used. The modern way is called _Logical Block Address_ (LBA). In LBA you just specify the sector number you want to get. LBA 0 is the first sector on the disk, etc.<sup>2</sup>

### Some housekeeping before we actually read from the disk

First create a text file and put in it whatever you want. This is going to be the thing that gets read from the disk. Create a _Makefile_<sup>3</sup>.

```make
all:
	nasm -f bin ./boot.asm -o ./boot.bin
	dd if=./message.txt >> ./boot.bin
	dd if=/dev/zero bs=512 count=1 >> ./boot.bin
```

The first line is same assemble command we've already been using. The second line puts the content of _message.txt_ onto the end of our binary, and the third pads the binary out with null characters until it's 512 bytes, and therefore a valid sector. You can type `make` at the command line to compile the project. You can see the content of the binary with `hexdump -C ./boot.bin > hex.txt`, and opening the text file.

### Time to actually read from the disk

We'll be using interrupt `13h/02h`: "Disk - Read Sectors into Memory". Looking at the expected register values that [Ralph Brown](http://www.ctyme.com/intr/rb-0607.htm) provides<sup>4</sup> we can get to the follow code:

```assembly
    mov ah, 02h
    mov al, 1
    mov ch, 0
    mov dh, 0
    mov cl, 2
    mov bx, buffer
    int 0x13
    jc error ; if carry flag is set, meaning load failed

    mov si, buffer
    call print

; snip to end of file

error_message: db 'Failed to load sector', 0

times 510-($ - $$) db 0 ; Pad to 510th byte
dw 0xAA55 ; dw=define word. Puts bootloader signal

buffer:
```

Here we set up the registers as they need to be to read our message from the 2nd sector (`cl`) of the disk into the `buffer` label in memory<sup>5</sup>. Then we call the interrupt `0x13`. `jc` handles the error condition.

</div>
<div class="sidenotes">

<sup>1</sup> We already encountered sectors when talking about how the bootloader is loaded.

![Disk](../../images/2021_03_21_kerneldev/disk.png)

<sup>2</sup> Since you can only read in sectors, to calculate a specific place on the disk using LBA you need to calculate the sector and the offset. This is simply a matter of getting the quotient and the modulus. So to get to byte 58376 you calculate the LBA sector by `58376/512=114`, and the offset as `58376%512=8`

<sup>3</sup> Make is a language unto itself, intended to simplify the compilation of project with multiple files.

<sup>4</sup> `AH` = 02h, `AL` = number of sectors to read, `CH` = cylinder number, `CL` = sector number, `DH` = head number, `ES:BX` -> data buffer. Return: `CF` set on error

<sup>5</sup> Note that the data is put into `ES:BX`, the Extra Segment. We have set this to `0x7c0`, which is the right place.
</div>
</div>


<div class="tufte-section">
<div class="main-text">
</div>
<div class="sidenotes">
</div>
</div>


<div class="tufte-section">
<div class="main-text">
</div>
<div class="sidenotes">
</div>
</div>


<div class="tufte-section">
<div class="main-text">
</div>
<div class="sidenotes">
</div>
</div>


<div class="tufte-section">
<div class="main-text">
</div>
<div class="sidenotes">
</div>
</div>


<div class="tufte-section">
<div class="main-text">
</div>
<div class="sidenotes">
</div>
</div>


<div class="tufte-section">
<div class="main-text">
</div>
<div class="sidenotes">
</div>
</div>


<div class="tufte-section">
<div class="main-text">
</div>
<div class="sidenotes">
</div>
</div>


<div class="tufte-section">
<div class="main-text">
</div>
<div class="sidenotes">
</div>
</div>


<div class="tufte-section">
<div class="main-text">
</div>
<div class="sidenotes">
</div>
</div>

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
