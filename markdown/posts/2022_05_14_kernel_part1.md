# Kernel Development Part 1: Booting into Real Mode

This is based on a [course](https://dragonzap.com/course/developing-a-multithreaded-kernel-from-scratch) which guides the reader through the development of a full OS kernel, from a Hello World Bootloader to a multitasking kernel with a FAT16 file system. The first 11 lessons are available for free on [YouTube](https://www.youtube.com/watch?v=HNIg3TXfdX8&list=PLrGN1Qi7t67V-9uXzj4VSQCffntfvn42v)

## What is memory?
Memory is a piece of hardware that allows computers to store information. Programs can read and write to _Random Access Memory_ (RAM). It's only used for temporary storage, such as for variable storage of the programs you write. RAM is wiped when you shut down the computer. _Read Only Memory_ (ROM) does _not_ vanish when you shut down the computer. But as the name suggests, you can't write to it. In a home PC, the BIOS program is stored in ROM.

Memory is generally accessed in a linear fashion. The data is stored in order. The way your processor accesses memory abstracts this.

## The Boot Process and Real Mode
The boot process has three steps:

1. The BIOS[^1] program is executed from ROM/BIOS Chip
2. The BIOS loads the _Bootloader_ into special memory address `0x7C00`
3. The Bootloader loads the _Kernel_

![booting](../../images/2022_03_21_kerneldev/booting.png)

[^1]: Basic Input Output System

When the computer is switched on, the CPU will read from the BIOS ROM and start executing instructions it finds there. The BIOS usually loads itself into RAM for performance reasons and will continue to execute from there. The BIOS also identifies and initializes the computer's hardware, such as disk drivers.

The final thing the BIOS does is try to find a Bootloader. It searches all available storage mediums - USB drives, hard disks - for the magic boot signature `0x55AA`. It will look in the last bytes of the first sector[^2] and if the signature is found, it will load that sector into address `0x7C00`, and the CPU will start to execute from that address.

[^2]: A sector is a block of storage. For example, a hard disk is made up of 512 byte sectors. The BIOS will look in the byte addresses 511 and 512 of the sector.

When a computer first boots, it does so in _"Real Mode"_. This is a very limited 'compatibility' mode, with access to only 1Mb of memory, and it can only execute 16 bit code. The Bootloader is a small program whose job is to put the computer into _"Protected Mode"_, which allows 32 bit code and access to 4Gb of memory, and then to load the kernel of an operating system.

The BIOS contains routines that the bootloader uses to boot the kernel. The interfaces of BIOS routines are generic and standardized across manufacturers.


## Getting setup for Kernel development

All development will be done on Ubuntu Linux. First, make sure your repositories are up to date. Then, install nasm[^4]. Finally, we will use the QEmu emulator to run our bootloader and kernel. Test that it runs using the below commands. A new window will pop up, but since there are no disks attached it won't be able to boot.

[^4]: NASM is the "Netwide Assembler", and assembler for the x86 CPU architecture, compatible with nearly every modern platform. https://nasm.us

```bash
sudo apt update
sudo apt install nasm

sudo apt install qemu-system-x86
qemu-system-x86_64
```

## Assembly language
This is a short refresher on what assembly language is. Or, for me, basically an introduction to it, since I've never written it before.

Your processor has an _instruction set_, or machine codes. Assembly language gets passed through an _assembler_, and machine codes your processor understands come out the other side. Use [Emu8086](https://emu8086-microprocessor-emulator.en.softonic.com/) to easily test assembly programs.[^5]

[^5]: For linux users, it works fine with Wine.

Write the following program in your emulator:

```asm
mov ah, 0eh
mov al, 'A'
int 10h
```

This is a program that outputs 'A' to the screen. `mov X Y` moves data Y to register[^6] X. `ah` ("Accumulator High") and `al` ("Accumulator Low") are registers storing one byte. So here we move `0eh`[^7]  into `ah`, and the character 'A' (65) into `al`. `int X` means _"interrupt with code X"_. The code `10h` outputs things to the screen[^8]

[^6]: A register is a storage location in the processor.

[^7]: `0e` in hex, 14 in decimal

[^8]: Specifically, 10h is a _BIOS routine_: A function that is defined in the BIOs, and can only be used in compatibility mode.

So we have a program that outputs a _character_ to a screen. Now we need to output multiple characters. Here is our next program.

```asm
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

## A Hello World Bootloader
Next we will turn our "hello world" program into a bootloader. That is, the program will be loaded by the BIOS and, when we boot the machine, it will show "Hello World!" on the screen.

Create a new folder / project _PeachOS_. Make a new file _boot.asm_, and add the following code:

```asm
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

This is very similar to our non-bootloader code. There are a couple of organizational changes (`print_char` has been extracted into a subroutine, things have been moved around a bit), and a couple that are required to make this function as a bootloader. `ORG 0x7C00`  sets the 'origin' or 'starting location' where your program is loaded into memory. Bootloaders are always loaded to `0x7C00`. `BITS 16` tells the processor that it should interpret the program in 16bit mode ("Real Mode", or "Compatibility Mode"). The last two lines pad out the program with zeros, and then put the value `0x55AA` (the 'signal' that this is a bootloader) to the last 2 bytes of the sector.[^9]

[^9]: Note that we say `0xAA55` in the code. This is because bytes are loaded 'backwards', with `55h` being loaded first.

Assemble your bootloader and boot it with

```bash
nasm -f bin ./boot.asm -o ./boot.bin
ndisasm boot.bin #look at the machine code
qemu-system-x86_64 -hda ./boot.bin
```

## Working with the Segmentation Memory Model
We have seen that the pointer registers in the processor are 2 bytes. That means your instruction pointer for example, which is 2 bytes, can point to memory locations _(addresses)_ between bytes number 0 and (2^16) 65,535. However we've seen that in real mode, the processor has access to 1Mb of memory, or 1,048,576 bytes. How can we get our pointers to point to values above 65,535?

The answer is the _Segmentation Memory Model_. Memory is accessed by the combination of a _segment_ and an _offset_. This is what the _segment registers_ are for. There are 4 in the 8086: Code segment `cs`, Data segment `ds`, Extra segment `es` and Stack segment `ss`. The segment and offset can be combined to calculate the _absolute offset_ (the actual memory location in RAM) by multiplying the segment by 16 (A left shift in hex) and adding the offset[^10]. For example, if your segment is `0x7C0` and your offset (instruction pointer) and origin are both zero. The absolute memory address your program will start executing at is `0x7C0 * 16 = 0x7C00`. If your offset is `0xFF`, the absolute address will be `0x7CFF`. If segment is `0xF000` and offset is `0xFFFF`, the absolute memory address is `FFFFF`, or 1,048,575. This is how you address a megabyte of memory using two 16bit registers. Note that this model means that you can get to an address in multiple ways. For example, if your segment is `0x7CF` and offset is `0x0F`, the absolute address is also `0x7CFF`.

[^10]: Note that `ORG` or origin is also factored in.

Different instructions in the processor's instruction set use different combinations of registers to determine which absolute address to look at. For example, `lodsb` which we've already seen uses the data segment register and the source index register (shorthanded to `ds:si`).

Segment registers can be used in source using the following convention:

```asm
mov byte al, [es:32]
```

This will move the byte located in `es:32` into `al`.

One thing we need to do with our bootloader is to make sure all the segment registered are initialized to the values we want. The BIOS and interrupts can sometimes mess with these. Change the origin to 0. Give a `jmp` instruction to `0x7c0`, which changes the instruction pointer. Change data and extra segments to `0x7c0`. Change the stack segment to `0x00` and the stack _pointer_ to `0x7c00`.

```asm
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
3. The first instruction jumps to `0x7c0:start`[^11].

[^11]: Frankly I don't get why - isn't this redundant?

