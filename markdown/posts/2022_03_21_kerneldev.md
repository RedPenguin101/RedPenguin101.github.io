% Kernel Development

This is based on a [course](https://dragonzap.com/course/developing-a-multithreaded-kernel-from-scratch?coupon=YOUTUBEKERNEL2022) which guides the reader through the development of a full OS kernel, from a Hello World Bootloader to a multitasking kernel with a FAT16 file system. The first 11 lessons are available for free on [YouTube](https://www.youtube.com/watch?v=HNIg3TXfdX8&list=PLrGN1Qi7t67V-9uXzj4VSQCffntfvn42v)

## What is memory?

Memory is a piece of hardware that allows computers to store information. Programs can read and write to _Random Access Memory_ (RAM). It's only used for temporary storage, such as for variable storage of the programs you write. RAM is wiped when you shut down the computer. _Read Only Memory_ (ROM) does _not_ vanish when you shut down the computer. But as the name suggests, you can't write to it. In a home PC, the BIOS program is stored in ROM.

Memory is generally accessed in a linear fashion. The data is stored in order. The way your processor accesses memory abstracts this.

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

<sup>2</sup> A sector is a block of storage. For example, a hard disk is made up of 512 byte sectors. The BIOS will look in the byte addresses 511 and 512 of the sector.
</div>
</div>

## Getting setup for Kernel development

<div class="tufte-section">
<div class="main-text">
All development will be done on Ubuntu Linux. First, make sure your repositories are up to date with. Then, install nasm<sup>1</sup>. Finally, we will use the QEmu emulator to run our bootloader and kernel. Test that it runs using the below commands. A new window will pop up, but since there are no disks attached it won't be able to boot.

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

## A Hello World Bootloader

Create a new folder / project _PeachOS_. Make a new file _boot.asm_.



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

