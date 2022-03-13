% Stanford on C

* [101](http://cslibrary.stanford.edu/101/EssentialC.pdf)
* [102](http://cslibrary.stanford.edu/102/PointersAndMemory.pdf)
* [103](http://cslibrary.stanford.edu/103/LinkedListBasics.pdf)

# 101: Essential C

## Introduction

* C is not forgiving. It will not help you
* It expect that you know how it works
* It is "simple" in that is has only a few components

## 1: Basic types and operators

### Integer types
* `char`: ASCII character, at least 8 bits. -128..127, or 0..255
* `short`: at least 16 bits, -32768..32767
* `int`: at least 16 bits, usually 32.
* `long`: at least 32 bits, ~ -2bn..2bn
* Generally, avoid unsigned, causes more problems than it solves
* pointers are sort of like an `unsigned long`, where the number is the (4 byte) memory address.
* C defines _minimum_ sizes for integers, not exact sizes. This can cause compatibility issues. Consider this if you're writing a program that will run on many manchines
* Chars are written in single quotes. `'A'` etc. or special chars like `\n`, `\t`
* Behind the scenes, just an 8bit integer value. 'A' is 65.
* A `long` constant can be designated like `24L`
* A constant can be written in hex with `0x10` (16), or Octal with `012` (leading zero) (10)
* C is weakly typed and will promote as required. `'b'+5` is fine. The char will be promoted to an int.
* C will also truncate e.g. from `char` to `int`, but not automatically:

```
double pi;
int i;
pi = 3.14159;
i = pi; // i is now 3
```

* There is no boolean, use int. 0 is false, non-zero is true.

### Floats
* `float`: single precision (32 bits, about 6 digits)
* `double`: double precision (64 bits, about 15 digits)
* constants (`x.y`) are doubles, unless suffixed with `f`.

### Comments
* `/* .. comments ..*/` can be multiline
* `//` (line comment) are technically from C++, but are usually supported.

### Variables
* Declare with `int num;` 
* Assign value with `num = 42`
* A variable corresponds to an area or 'box' in memory, which can contain a value of the specified type.
* The memory location is not initialized, it'll contain some random thing until you set it.
* Declare multiple with `int a, b, c;`
* assignment is also an expression returning the assigned value: `y = (x = 2 * x)` is valid. Don't do this.

### Casting
* cast like `score = ((double)score/20) * 100`. Casts `int score` to a double before doing the division. Though `(score/20.0)` will autocast `score` to a double.

### Operators
* `+-/*`.
* `%` mod
* Unary increments: `var++`, `var--` unary operators
* Also `++var`, `--var`. These return expressions, so can be used in, for example, loop conditions. Don't though.
* Relationals: `==`, `!=`, `>`, `>=` etc.
* Logicals: `!`, `&&`, `||`. Evaluate left to right, and short circuit.
* Bitwise: `~` (bitwise negate), `&`, `|`, `^` (XOR), 
* `>>` and `<<` are shift left/right. Equivalent to dividing by power 2, multiplying by power 2.

## 2: Control
* `{}` are for grouping. 
* `if (<expression) <statment>`
* `if (<expression) {<statments>}`
* `if (<expression) {<statment>} else {<statement>}`
* Ternary: `<expr1> ? <expr2> : <expr3>`
* `min = (x < y) ? x : y;`
* switch (awful). Fall through. Breaks necessary to exit.
* `while(<expression>){<statement>}`
* `do {<statement>} while (<expression>)`. Test condition at end. Don't use it.
* `for (<init>; <cont>; <action>){<statment>}`
* `for (i=0; i<10; i++){<statment>}`
* `break` will move control in loop to outer block. Use sparingly. Doesn't work for `if`s.
* `continue` will skip to the bottom of the loop. Use _even more_ sparingly, usually prefer an if.


## 3: Data types
* Arrays and Records (structures)
* Define with `struct fraction {int numerator; int denominator;};`. Remember the semicolon at the bottom.
* Declare with `struct fraction f1`
* Access with .: `f1.numerator`
* Assignment copies (i.e. they don't point to same memory loc): `f1=f2`
* `==` doesn't work on structs.
* `int scores[100]` declares a 100 length array of integers
* `scores[0]=13;` sets the first element
* `scores[99]=42;` sets the last element
* The name of an array refers to the whole array. Technically, it is a pointer to the memory address of the first element.
* `int board[10][10]` is a 2d array. This is just sugar, since in memory it's just a contiguous block of 100 integers. `board[1][8]` is next to `board[1][9]`.
* `struct fraction numbers[1000]` declares an array of structs.

### Pointers
* A pointer is a value which contains a memory location.
* `*` is used to indicate a pointer. e.g. a `char*` is a pointer to a char. They are declared like any other variable `int* intPtr`.
* In an expression, use `*x` (where `x` is a pointer) to dereference.
* `->` is sugar for deref and access. `f1->numerator` (where `f1` is a pointer).
* `&` computes a pointer to the argument to its right. Use when you have some memory and what a pointer to that memory

```c
void foo() {
  int* p; // a pointer to an integer
  int i; 

  i = 10;
  p = &i; // set p to point to i
  *p = 13; // set the thing p points to (i) to 13 
  // i IS 13. *p IS i. So *p IS 13.
}
```

* A pointer assigned `0` means it doesn't have a pointee (aka null pointer)
* (`NULL` is actually just a constant with value 0)
* Don't try to dereference a null pointer.

### Strings
* Minimal support
* Just arrays of char with a null (`\0`) terminator
* assignment (`=`) doesn't copy strings. use `strcpy`.

```c
char string[1000];
int len;

strcpy(string, "binky");
// string = [b,i,n,k,y,\0,x,x,x,x,...]
len = strlen(string);
// len = 5

int i, j;
char temp;
// string reversal.
for (i=0, j=len-1; i<j; i++, j--) {
  temp = string[i];
  string[i] = string[j];
  string[j] = temp;
}
```

* `char string[1000]` is either wasteful or dangerous in many cases. Heap allocation of strings is usually preferable.
* note that if `char string[10]`, the type of `string` is essentially a `char*`, since it's a pointer to the memory location of the first char.

### TypeDef
* Defines a type
* `typedef struct fraction Fraction` to define the type
* `Fraction f1;` to declare.
* really just an alias mechanism.

## 4: Functions
* Functions in a file are in a 'namespace'
* the special function `main` is called when program executes.
* Convention is to start function with uppercase.
* `static int Twice(int num) { return(2 * num); }`
* `static` means private to namespace
* `void` (return) type means "nothing".
* functions can accept a void, but generally we just write `int MyFunc()`
* functions are pass-by-value (i.e. copied)
* Pass by reference is done with pointers

```c
void Swap(int* x, int* y) {
  int temp;
  temp = *x;
  *x = *y;
  *y = temp;
}

int a = 1;
int b = 2;
Swap(&a, &b); // & Computes pointers to a and b
```

## 5: Other stuff
* A 'prototype' gives a signature but not an implementation.
* `int Twice(int num);` (note semicolon)
* What the hell is it for?
* The preprocessor does stuff before the main source code is passed to the compile. Most common are `#define` (set up symbolic replacements) and `#include "foo.h"` or `#include <foo.h>`.
* `foo.h` should contain prototypes for the 'static' functions in `foo.c` (which should have `#include "foo.h"`
* don't `#include` h files in other h files. It can cause compile errors.
* Some compilers allow you to do `#pragma once` which will avoid the issue
* use `assert(...)` for safety. Some compiler settings will exclude these, since they're for testing.

## 6: Advanced arrays and pointers

* Since an array variable is just a pointer to a memory location where you've declared an array to start, you can play games with pointers. But be careful.

```c
int intArray[6];
intArray[3] = 13;
intArray // has type int*
intArray + 3 // gives a pointer to the value at intArray[3]
intArray[3] // and
*(intArray + 3) // are equivalent.
```

* Though an array variable is a pointer, it is a _const_ pointer, meaning you can't reassign it.

### Heap
* The functions are in `<stdlib.h>`
* As opposed to stack, heap can be dynamically allocated to at runtime.
* `void* malloc(size_t size)`: request a contiguous block of memory, of `size` (in bytes). A `size_t` is just an unsigned long. Returns a pointer to the block (or null). Since the return type is `void*`, you'll probably need to cast the pointer.
* `void free(void* block)`: Takes a pointer created by malloc and returns it to the heap.
* `void* realloc(void* block, size_t size)`: For efficiency, you can reuse allocated blocks.
* You can allocate arrays on the heap manually, and use them basically identically to stack arrays.

```c
int a[1000];
int* b;
// manually allocating an array on the heap.
b = (int*) malloc(sizeof(int) * 1000);
assert(b!=NULL); // check alloc succeeded.
a[123] = 13;
b[123] = 13;
free(b);
```
* The difference is, you can define the size of the heap array at runtime, and change its size at runtime with `realloc()`
* if you forget to allocate, forget to deallocate, or do either of those things wrong, the program will crash at runtime (and is a pig to debug).
* heap arrays work well with strings because it allows runtime sizing, meaning you can size the array properly. No need to `char string[1000]`

```c
#include <string.h>

char* MakeStringInHeap(const char* source) {
  char* newString;
  newString = (char*) malloc(strlen(source)+1); // +1 for terminator
  assert(newString != NULL);
  strcpy(newString, source);
  return(newString);
  // Note caller takes ownership, is responsible for freeing memory.
}
```

## 7: Libraries

* stdio: file input/output
* ctype: character tests
* string: strings!
* math: math functions
* stdlib: utils, including heap and random numbers
* assert: assert macro
* stdarg: support for functions with variable numbers of arguments
* time: date and time.
* limits, float: constants with type range values.

### stdio

* `FILE* fopen(const char* fname, const char* mode)`: mode is r,w,a. returns NULL on error.
* `int fclose(FILE* file)` Returns EOF on error.
* `int fgetc(FILE* in)` next character from file _but as an int_, since it might not be an `char`
* `char* fgets(char* dest, int n, FILE* in)` read next line, at most n-1 chars, stops at (and includes) `\n`. 
* `ing fputc(int ch, FILE* out);` write ch to file. Returns `ch` (EOF on error)
* `int printf(const char* format_string, ...)` `%d` = int, Ld=long int, s=string, f=double, c=char.
* `int scanf(const char* format, ...)` opposite of printf. Pattern matches the input against format string. Has variants `sscanf` (reads from a string) and `fscanf` which reads from a file.

```c
int num;
char s1[1000];
char s2[1000];
scanf("hello %d %s %s", &num, s1 s2);
// Be careful with pointers!
```

### ctype
* simple character tests/operations
* `isalpha`, islower, isupper, isspace (incl. tab, newline etc), isdigit
* toupper/tolower

### string
* None of these manage your memory for you
* `strlen`, `strcpy` already seen
* `size_t strlcpy(char* dest, const char* source, size_t dest_size)` string copy which truncates at the destination size.
* `char* strcat(char* dest, const char* source)` appends from source to dest.
* `int strcmp(const char* a, const char* b)` compares strings and returns -1, 0 (equal), 1. REPEAT: ZERO (aka FALSE) MEANS EQUAL
* `char* strchr(const char* searchIn, char ch)` search for ch in searchIn, returns pointer to character or NULL
* `strstr` similar, searches for substring.

### stdlib
* `int rand()` random integer between 0 and (usually) 32767
* `void srand(unsigned int seed)` sets seed that `rand()` uses.
* `malloc`,`free`,`realloc` we've seen
* `exit(int status)`. 0 signals normal program termination
* `void* bsearch(const void* key, const void* base, size_t len, size_t elem_size, <compare_function>)` binary search array. compare function is `int compare(const void* a, const void* b)` and should return -1,0,1. Returns pointer to found element.
* `void qsort(void* base, size_t len, size_t elem_size, <comp>)` quicksort.

# 102: Pointers and Memory
* Why pointers? Allow different sections of code to share information, and enables linked data structures (linked lists, trees)
* A pointer is a reference to another value.
* Or, a variable with a value that is the memory location of another variable.
* To get from a pointer to a pointee, _dereference_.
* A NULL pointer is pointer that points to nothing (value of the pointer is 0 in C). Deref a null pointer is a runtime error.
* assigning a pointer to another pointer makes them point at the same pointee. Two pointers that point to the same pointee are "sharing". This is often the goal of using pointers.
* Draw memory drawing to help think about your pointer code!
* A pointer starts uninitialized (or "Bad"). Always assign a pointer as soon as you declare it.
* The _pointee_ of a pointer also needs to be initialized for it to work properly.
* `&x` returns a pointer to x (or "computes a reference").
* `*p` dereferences a pointer, and returns the value of the pointee.

```c
int a = 1;
int b = 2;
int c = 3;
int* p;
int* q;
p = &a;
q = &b;

c = *p; // assign c the value of *p, which is the value of a, i.e. 1
p = q; // assigning q to p means that p now points to b.
*p = 13; // assigns b (*p) as 13
// a[1]   |-p
// b[13] <--q
// c[1]
```

## 2: Local memory

