# Editing Code in Emacs

When you write code, you want to focus on the code, not on the text of
the code. This means a) you have to have a good text editing setup,
and b) you need to have a muscle-memory level instinct for using that
setup. The second comes with practice and with _consistency_ (i.e. not
changing your config too much too quickly). The first is what I will
talk about here.

This document is meant for people who are current users of, or at
least slightly familiar with Emacs. I won't spend much time explaining
Emacs basics - for example how incremental search, or compilation
buffers work (I would recommend [Mastering
Emacs](https://www.masteringemacs.org/book) for that). But I will give
rationales for the choices I've made in encouraging or discouraging
certain patterns.

You can read this in two ways: The general Emacs commands I use to try
to edit the text of programs efficiently, and the specific keybinds I
use in my modal 'command' mode to make those commands as convenient as
possible.

## No Mouse, No Arrows

All text editing practices rely on minimising the work your fingers do
by minimising the number of keystrokes and keeping your fingers as
close to the home row as possible. This means no arrow keys and no
mouse. This can be enforced by remapping your arrow keys to `ignore`,
and by installing the package `disable-mouse`.

## Modal Editing: Command and Insert Modes

Editing code is different from writing prose in that you spend a lot
more time moving around the document, and moving things around _in_
the document, than actually writing text. The actions for moving are
more important than the actions for typing, and should therefore be
closer to hand. This is the premise of _modal editing_: the "default"
actions of most keyboard keys are to move, not to type. For example in
the default 'mode', hitting 'a' doesn't type the 'a' character, it
moves the cursor to the start of the line. To actually type things,
you need to hit a special key which puts you in 'insert' mode. Then
when you are finished typing, you hit another key which puts you in
the default (or 'command') mode.

My modal system is [custom
written](https://github.com/RedPenguin101/dotfiles/blob/main/modal.el)
and very lightweight - about 150 lines, not including the keybinds
themselves. I recommend using _a_ modal system, if not mine then
someone elses, such as Evil or Meow. But if you really dislike them,
you can still do everything I describe here in vanilla emacs, and most
of the commands already have default keybinds. There are only four
'custom' functions I use: the half page scrolls, and the
kill-whole-word/sexp. And all are very simple.

## A note on defaults

A problem with customised setups is that they mean you can't pick up
your friend's Emacs setup and use it, because your muscle memory will
cause you to hit all the wrong keys. This effect can be mitigated by
sticking with the 'language' of the system. Emacs has pretty clear (if
arguably not very good) conventions for most of it's keys: `f` means
forward, `n` means next, `C-g` is always 'cancel'. My setup tries to
stick with these conventions as much as possible. `f` in command mode
is 'forward-word'. `n` is 'next line'.

Additionally there is basically no remapping _for insert mode_. The
idea being that editing in a vanilla Emacs is the same as editing
using only insert mode in my setup. I find that you spend a fair
amount of time navigating from _within_ insert mode even in my setup,
so you won't lose your muscle memory.

## Leaders

The most common actions for moving around the screen are on a single
keystroke on command mode. For example, to go to the next line, you
hit `n`. To go forward by a word, press `f`.

Less common, but still important commands are usually two or three
keystrokes. For example, save file is `vs`. Kill word is `kf`. In
these cases, the first key is a 'leader' key. I use a few leader keys:

- `v`: A general leader key, but mostly for file, buffer and window
  operations.
- `k`: Kill leader: most of the kill commands are under this.
- `s`: Search leader: most searches are under this
- `vp`: Project leader: contains several operations that are useful
  when working on a 'project' that consists of many files, which is
  very common with programming projects.

## Getting in and out of insert mode

To transition from command to insert mode, press `i`. To transition
from insert to command mode, press `C-j`.

There are a few more ways to get into insert mode:

- `I`: Insert after character
- `O`: Insert in overwrite mode (overwrite mode will be cancelled when
  you return to command mode)
- `A`: Insert at start of (indented) line
- `E`: Insert at end of line
- `C-RET`: Newline and insert
- `S-RET`: Newline above and insert

## Moving Vertically

I recommend you set up relative line numbers, and global-hl-line-mode
so you can clearly see which line your cursor is on and how far away
each line is.

```elisp
(setq-default display-line-numbers-type 'relative)
(global-display-line-numbers-mode 1)
(global-hl-line-mode +1)
```

In command mode press `n` to move to the next line, and `p` to move to
the previous line. Often they will be used in conjunction with a
numeric prefix: type `12n` to move down 12 lines. This number-prefix
pattern is general: you can do most commands multiple times by typing
digits before typing the command.

`r` moves up by a half page, and `t` moves down by a half page while
keeping the cursor line in the middle of the screen. These are used
in preference to the usual `scroll-up` and `scroll-down` commands,
which move so much you have to spend a second reorienting.

Two useful and related actions are `recenter-top-bottom` and
`move-to-window-line-top-bottom`. These are bound to `l` and `L`
respectively. `l` moves the screen around the current highlighted
line - first centring the screen around the hl-line, then putting the
hl-line at the top of the screen, then at the bottom. It's best to
just try it out. `L` is sort of the opposite, it moves the _cursor_
around the screen, first to the center, then to the top, then to the
bottom.

`.` and `,` are 'beginning-of-defun' and 'end-of-defun'. You can think
of these as moving by a top level 'block'. These are usually pretty
useful, but depend on your language mode having a good definition for
what a 'block' is.

Less often used, but occasionally useful, are `<` and `>` for moving
to the beginning and end of the current buffer.

## Moving Horizontally

Moving horizontally is important, but when programming you should
really avoid using these commands too much in favour of moving in
larger syntactic units - see the later sections on moving by
expression and search.

You should turn on subword mode:

```elisp
(global-subword-mode 1)
```

When moving horizontally, try to move in as large a unit as you can.
You should almost never move left or right by an individual character.
The smallest general unit is a "word" - similar to how most editors
will use `Ctrl-Right` to move right by a word. To move forward by a
word, press `f`. To move backward by a word, press `b`.

The definition of a 'word' in Emacs can be a bit tricky, especially
when it comes to programming. `foo_bar_baz` is _three_ words.
`fooBarBaz` (if you've got subword mode turned on) is also three
words. So for either of these, if your cursor is on the `f` of `foo`,
pressing `f` to go forward will put you before the `baz` symbol. This
is handy for changing things within a long variable name. But it's not
great for rapid navigation. Which is why I recommend moving by
_expression_ over moving by _word_.

If you must move by a single character, use `C-f` and `C-b`
respectively.

`e` moves to the end of the current line. `a` moves to the start of
the current line, but generally you should prefer `m`, which moves to
the first non-whitespace character of the line - which is usually what
you want when programming. However, if I'm trying to move to the start
or end of a line, it's usually because I want to type something there.
And for doing that you can use `A` and `E` respectively, which will
move to the start or end of the line and immediately enter insert
mode.

This is it for moving strictly within a line. But for the various
reasons outlined above, you really you shouldn't use these too much.
There are better ways to move within a line: moving by expression and
moving by search.

## Moving by Expression

S-Expressions, or Sexps, are a big thing in lisps and therefore in
Emacs. Most programming languages are syntactically 'blocks' of
symbols enclosed in different bracket types. Many use curly braces to
denote execution blocks - function bodies, loops, structure
definitions - square brackets to denote arrays, and parentheses to
denote parameter/argument lists. All fit the s-expression definition.
When you're moving around a program it can be useful to think in terms
of jumping in to, out of, over, or within those blocks. Emacs has lots
of commands for this, and there are extensions which add even more,
but I really only use four.

`j` moves forward by a sexp. If the cursor is over an opening bracket
of any kind, pressing `j` will jump _over_ that whole block. `h` will
do the same thing, but backwards. This can effectively be used as a
'jump to matching bracket' command.

If on a non-bracket character, these will jump forward or back by one
syntactic symbol. This should generally be preferred to moving by
_word_ because in most cases when programming you want to jump over
the symbol, not the word. For example if are at the start of the
variable name `foo_bar_baz`, unless you want to change something in
that variable, you probably want to jump over the whole thing. `j`
will do that, whereas `f` will jump you to `bar`.

The other two I use are 'down-list' (`d`) and up list (`u`). These
jump _into_ and _out of_ a block. For example if my editor looks like
this, where `|` is the cursor position:
`dele|te(state.im_temp_entity_buffer)`, and I hit `d`, the cursor will
be moved into the next block - in this case the argument list for
delete: `delete(|state.im_temp_entity_buffer)`. Pressing `u` will move
the the cursor _out_ of that list:
`delete(state.im_temp_entity_buffer)|`. This works on any type of
brackets. These can also be used with a negative argument (e.g. `-d`)
to go _back_ into and _back_ out of an expression. You can reverse the
above sequence with `-d`, resulting in
`delete(state.im_temp_entity_buffer|)`, and then `-u` resulting in
`delete|(state.im_temp_entity_buffer)`.

Using these sexp expressions when programming is usually far more
effective than using the horizontal movements like 'forward-word', and
you should get into the habit of preferring them.

## Moving by Search

Sexps are great, but really the best way to move more than a few words
around your buffer is to move by searching for the string of text you
want to jump to. If the location you want to jump to is on the screen,
this creates a sort of 'look at, jump to' dynamic, where you find
where your want your cursor to be with your eyes, type some of the
text at that location, and your cursor is now there. But it also works
great if the location you're looking for is off the screen.

The simplest commands are the usual 'isearch-forward' and
'isearch-backward'. The mappings for these are unchanged from standard
Emacs: `C-s` and `C-r`. There are packages which provide alternative
versions of this - 'jump-char' and 'avy', for example - but I find
these work fine.

Sometimes you're searching for something that is pretty common, and
using incremental search is a slog. In this case, you can use occur,
with `so`, which creates a buffer with all the instances of the search
term, hyperlinked so you can easily jump to that location.

How to use occur not specific to my setup, but is very useful to
learn, so I'll go into some detail. When you are in an occur buffer:

- `M-n` and `M-p` will move up and down, but _won't_ jump the original
  buffer to the relevant line
- `n` and `p` will do the same, but it _will_ update the original buffer
  to show the line
- `M-g M-n` and `M-g M-p` will not only update the original buffer to
  show the selected line, but it will make the original buffer active
  at that location. A bit hard to explain in words, but it's very
  useful, try it out.

The other useful thing about occur is that, while it's read only by
default, you can make it editable with `e`. And from here you can edit
_the original buffers_ from in the occur window. Huge. Get back to
read-only mode with `C-c C-c`

You can also create an occur window for multiple buffer with
`multi-occur-in-matching-buffers`. But I find that a bit fiddly. What
I would really like is a 'project-occur' which searches for all
instances of a term in a current project. But Emacs doesn't have that
built in that I'm aware, though I believe it's in the common
'projectile' external package. I use the 'ag' package and
silver-surfer search program to search project-wide for terms, but
it's not ideal.

## Registers and the Mark

Another way to quickly jump around a buffer is to use registers. These
are short lived 'bookmarks', which you can set and return to.
Typically I'll use these when I want to temporarily jump to another
location from a point I'll want to return to afterwards. For example,
jumping into a function from the calling location, then back out to
the calling location. Typically I'll hit `v SPC a` to set my current
location to the register `a`. Then jump to the other place. Then when
I'm done, `vja` will take me back to my original location. If I want
to chain these together, I'll use the registers `a`, `s` `d` and `f`
as a sort of 'stack' Often I'll also want to jump between two
locations repeatedly, so I'll set them up as `a` and `s`.

An alternative way to get the above behaviour is to the use the 'mark'
as a very transitory, but automatic, register. When you do most
'jumps' in emacs, e.g. using isearch, a temporary register called the
'mark' is created in the place you jumped from. Or, you can set it
manually using `gg`.Then, you can jump to that mark (resetting it to
the place you jumped from in the process) with `C-x C-x`. This is a
like the `a` and `s` pattern I described above, but with the advantage
that you don't have to set the register yourself. You can also 'pop'
the mark by hitting `C-u g`. And you can do this repeatedly by hitting
`C-u g g g`. The downside being that the mark is less permanent than
the registers, so you can accidental set it to something else, and
you'll find your jumps will take you somewhere you don't expect, which
is disorienting. For that reason I usually use manual registers.

## Find and replace

While you can use occur mode to do find-replace, generally it's easier
to use `sq` (query-replace). This is both standard emacs functionality
and works basically the same as other editors find-replace so I won't
go into how it works.

A variant on that is `vpq`, which is _project_ query-replace. It works
the same way, but runs through every file in your project, not just
the current buffer.

## Killing, or Cut Copy Paste

In the hierarchy of importance of operations in program text editing,
moving around the buffer is top, cut/copy/paste is second, and typing
is third.

We've seen that there are lots of options for moving around the screen
using different syntactic units. Moving and 'killing' (as emacs called
the operation that is usually called cut) are sort of 'twinned': for
each move, there is usually an equivalent kill. And in my setup they
are, where possible, on the same keys, just with a `k` prefix.

So `kf` is kill forward word, `kj` is kill forward sexp. A full list
is below, but if you just think about how you move by a certain
amount, you can usually get the equivalent kill function this way.

There are a few special cases for kills though. There is `kf` for kill
forward word and `kj` for kill forward sexp, often what you want to do
is kill the whole word/sexp _you are currently in_. These are the `ki`
(kill whole word) and `kn` (kill whole sexp) commands. Similarly, `ke`
will kill from your point to the end of the line, but more often you
will want to 'kill whole line' `kl`.

A convenient (though often inefficient) thing to do is kill all the
text in a highlighted region. You can do this is `kw` kill region. Or
you can _copy_ a region with `ks` kill save.

You will often find yourself wanting to kill from your cursor up to a
certain character. Emacs calls this a 'zap', and you can do it with
`kz` zap to character.

Finally, if you find yourself wanting to join the current line with
the line above it, `k6` will do that.

To paste, just hit `y` (for yank).

Here is the full list of kill commands.

- `kf` kill word
- `kb` kill back
- `kj` kill sexp
- `kn` kill inner sexp
- `kh` kill sexp back
- `ke` kill to end of line
- `kl` kill whole line
- `kw` kill region
- `ks` kill ring save
- `k6` join line
- `kr` kill rectangle
- `kz` zap to character
- `ki` kill inner word

## File and window operations

When programming you spend a lot of time jumping between files and
buffers within the 'project'. The project usually being defined as the
root of the source repo.

Most of these operations are mapped with the `v` leader key, and in
the case of commands that operate on the whole project, `vp`. None of
them are particularly unusual, so I'll just list them:

### Window commands

- `w` delete other windows
- `o` other window
- `v1` delete other window
- `v2` split window below
- `v3` split window right

### File commands

- `vf` find file
- `vpf` project find file
- `vs` save file
- `vps` save project files
- `vr` recent files (requires some custom setup)
- `vd` dired
- `vpd` project root dired

### Buffer commands

- `vk` kill buffer
- `vpk` project kill buffers
- `vb` switch buffer
- `vpb` project switch to buffer

## Other useful things that don't fit anywhere else

Macros are surprisingly usable in Emacs, though they are something of
an art. `v[` starts defining a macro, `v]` ends it. `vm` applies the
macro. You can apply it repeatedly with `vmmmmm...`

LSPs using Emacs LSP implementation eglot are something of a mixed
blessing in my experience. I usually keep it turned off. But sometimes
being able to use 'xref-find-definition' (`M-.`) and the improved tab
completion is too useful to ignore.

'comment-line' `;` I use all the time. If you have a region
highlighted, it will comment out the region.

`/` for 'undo', `v\` for whitespace cleanup. `q` for 'fill or
reindent' will usually tidy the formatting of whichever block you're
in. `x` is 'execute command'. `z` is repeat.

Rectangle editing is often useful. Highlight the region you want to
edit, and then `kr` to kill it, or `vt` to replace the rectangle with
the thing you type. I find this works for most cases I would use
multi-cursor in other editors.

`vv` opens the VC interface (magit, in my case).

I tend to use `sh` to highlight a phrase in a certain colour when I
want something I'm currently working on to show up clearly.

`vi` for imenu, and `vI` for imenu-to-buffer are reasonable ways to
browse your code by 'section', provided the major-mode implements it
properly.

I disable a bunch of commands I sometimes hit accidentally with
unpleasant consequences, most annoying the two 'suspend' shortcuts
`C-z` and `C-x C-z`.

## Non-editing Configuration

I have some other stuff in my configuration apart from the above
keybindings. But most of it is either very common (fixing where
temporary files are saved), or very specific to how I like to do
things and not good general advice. For example I turn off transient
mark mode, but I wouldn't recommend it generally.

Tab completion can be a pain to get it how you like it. I use this,
but it's not perfect:

```elisp
(setq-default indent-tabs-mode t)
(setq-default tab-width 4)
(setq tab-always-indent 'complete)
(setq tab-first-completion 'word)
```

I _would_ recommend relying on as few external packages as possible. I
use, and would recommend, these ones:

- `ag` an interface to the silver-surfer search program. This is a way
  to search for a term across a whole project. grep is a reasonable
  alternative, but I prefer the silver surfer. Use it with `sa`
- `diff-hl`: A utility for highlighting lines that have changed since
  your last commit.
- `magit`: makes using git bearable (or possible, for things like
  rebasing)
- `visible-mark`: indicate visually where the 'mark' is.

And a couple of other, language specific ones.

## Why not vim?

No reason other than I'm used to emacs. There's nothing here you
couldn't equally do with vim.

## My init.el

Is here: <https://github.com/RedPenguin101/dotfiles/blob/main/init.el>
