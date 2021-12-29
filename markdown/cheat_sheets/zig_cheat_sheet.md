# Zig cheat sheet

## Input streams

### Reader

```zig
const stdin = std.io.getStdIn();
while (try stdin.reader().readUntilDelimiterOrEof(&buffer, '\n')) |str| {...}
```

Writes the line to the provided buffer. Returns the unconsumed stream, to be reused (e.g. in the while loop).

### Tokenize

```zig
var line_it = std.mem.tokenize(line, " ");
```

Tokenize takes an input string and splits it on the delimiter(s). Delimiters are like an _or_, so `" ,"` would split on space or comma.

Tokenize returns an iterator.

Split is similar but not 100% the same.

## Parsing

```zig
const this_num = try std.fmt.parseInt(usize, num_str, 10);
```

## Language construct reference

### Try

`try x` is sugar for `x catch |err| return err`

### Optionals

Nullable types. `?T`. `null | T` union type. e.g. `?usize`

Unwrap null handling with `orelse`. `var b = a orelse 0;` 

`.?` is sugar for `orelse unreachable`. Unreachable will throw, so use this when you want to enforce that unwrapping a null is illegal.

### Iterators

```zig
const dir = line_it.next().?
```

### Pattern match

```zig
switch (dir[0]) {
    'f' => x += num,
    'u' => y -= num,
    'd' => y += num,
    else => @panic("couldn't match direction"),
}
```
