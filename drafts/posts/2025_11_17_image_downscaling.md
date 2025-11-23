# Brogue's SDL Tile Downscaling Algorithm

I was looking through the source code for the game Brogue, and trying
to understand their procedure for downscaling their sprite tiles to
arbitrary sizes. In this post I write a program which does that
downscaling and explain how it works.

## What does the algorithm do?

Brogue is a 'Roguelike' game where the 'glyphs' written to the screen
are rectangular tiles with (mostly) ASCII characters. The source for
these tiles is a greyscale 'tiles.png' file containing a grid of
images representing letters, punctuation as well as actual 'pictoral'
representations for some things. tiles.png contains 16 columns and 24
rows. Each column is 128 pixels wide and 232 pixels tall, for a total
image size of 2048x5568 pixels.

On the screen, the tiles are drawn much smaller than this. A typical
tile size might be 10x18. This means that the tiles will have many
fewer pixels to represent the image. SDL (which is the renderer Brogue
uses) has functions which do this kind of downscaling, but the results
are often pretty poor. 

Here's the results of one of them. On the left is the full scaled PNG
source tile for 'A'. On the right is the 'A' tile scaled down using
SDL's 'BlitScaled' function - then scaled up again when drawn so the
sizes are comparible.

![Image1]()

Not great. So Brogue has written a custom algorithm to do the
downscaling.

Here's the algorithm, take a look:
[downscaleTile](https://github.com/tmewett/BrogueCE/blob/af0d25995221a51591ae7da4ba95c2fd28b1acc0/src/platform/tiles.c#L167)

Yeah, me neither.

There are a few things that make this hard to parse.

1. There is some genuinely complex stuff going on in there
2. The variable naming is often pretty unhelpful.
3. Some of the bit-level stuff is, in my opinion, unecessary given how
   infrequently this function will be run, i.e. whenever the window is
   resized. It's perfectly fast without that.

(It should be noted that the comments in this codebase are
_excellent_)

To deal with all of these, I'm going to start off with the simplest
version and build up all the complexities.

A quick note: the Brogue algorithm maps a _greyscale_ image to a
_alpha channel_ image. That makes sense in the context of a game,
where it's used as a mask. I'm just mapping a greyscale to greyscale.

## Version 1: Linear Interpolation and Average Sum of Squares

Version 1 introduces the concept of the 'mappings' and 'sum of
squares'.

The idea behind the mapping is pretty intuitive: for each column of
pixels in the source image, we decide which column in the destination
image it will map to. Because the destination image is smaller than
the source, it will be a many-to-one mapping: several columns in the
source image will map to each individual column in the destination
image.

The simplest way to make this mapping is to multiply each source
column value by the ratio of the dest tile width and source tile
width.

The next question: now we know _where_ each source pixel goes in the
destination image, _what_ do we put in each destination pixel? The
obvious answer is: the average of all the source pixels that map into
it. This is nearly what we do: we actually put in the square root of
the average of the squares. The comment gives the explanation of
that - some color-theory thing I don't fully understand, but makes
sense:

> The downscaling is performed in linear color space, rather than in
> gamma-compressed space which would cause dimming (average of a black
> pixel and a white pixel should be 50% grey = 0.5 luminance = 187
> sRGB, not 128). To simplify the computation, we assume a gamma of
> 2.0.

```odin
downscale_tile_v1 :: proc(source:^SDL.Surface, source_tile_width, source_tile_height: int,
dest:^SDL.Surface, dest_tile_width, dest_tile_height: int,
tile_row, tile_col: int)
{
	source_pixels := cast([^]u32)source.pixels
	dest_pixels   := cast([^]u32)dest.pixels

	// First, we create mappings which tells us, for each row and column in the
	// source tile, which pixel in the dest tile row/col that maps to. It will be
	// many-to-one mapping, something like [0,0,0,0,1,1,1,1,2,2,2,2...]
	col_mapping := make([]int, source_tile_width, context.temp_allocator)
	row_mapping := make([]int, source_tile_height, context.temp_allocator)

	// interpolation of x and y coords
	for col in 0..<source_tile_width  do col_mapping[col] = (col*dest_tile_width)/source_tile_width
	for row in 0..<source_tile_height do row_mapping[row] = (row*dest_tile_height)/source_tile_height

	// for each pixel in the destination, we will track the number of pixels in the source that map to it,
	// and the intensity of the value of the source, as represented by the square of the value
	// of one of the colors (both input and output are greyscale, so it doesn't matter which one).
	//
	// The value of the destination is then the sqrt of the average of the sum of squares.

	counter        := make([]u64, dest_tile_width*dest_tile_height, context.temp_allocator)
	sum_of_squares := make([]u64, dest_tile_width*dest_tile_height, context.temp_allocator)

	// calculating the counter and sum of squares, adding them to the accumulator
	for acc_row, src_row in row_mapping {
		for acc_col, src_col in col_mapping {
			src_idx := (tile_col*source_tile_width)+(tile_row*source_tile_height+src_row)*PNG_WIDTH + src_col
			acc_idx := (acc_row*dest_tile_width)+acc_col

			intensity := u64(source_pixels[src_idx] & 0xff)
			counter[acc_idx]        += 1
			sum_of_squares[acc_idx] += intensity*intensity
		}
	}

	// convert accumulator to image intensity
	dest_width := int(dest.w)
	for row in 0..<dest_tile_height {
		for col in 0..<dest_tile_width {
			dst_idx := (tile_col*dest_tile_width) + (tile_row*dest_tile_height+row)*dest_width + col
			acc_idx := row*dest_tile_width + col

			count := counter[acc_idx]
			sos := sum_of_squares[acc_idx]
			avg := 0 if count == 0 else sos/count

			intensity := clamp(u32(math.round(math.sqrt(f64(avg)))), 0, 255)
			// convert to a greyscale RGB value with full alpha
			dest_pixels[dst_idx] = ((intensity) | (intensity << 8) | (intensity << 16) | (0xff << 24))
		}
	}
}
```

