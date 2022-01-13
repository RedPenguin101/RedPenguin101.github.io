% A program for projecting 3d polyhedrons to 2d images

# Introduction

Inspired by [Mathematics for Programmers](https://redpenguin101.github.io/books/book_mathematics_for_programmers.html) I want a Clojure library that can project 3 dimensional polyhedrons onto 2 dimensional images.

The actual displaying will be done with [Clojure2d](https://clojure2d.github.io/clojure2d/), which I'll write a thin DSL around.

The book _partially_ described a method for this, but with some significant limitations. My goal is to generalize the approach.

This is the list of functionality I want, as well as things I don't want:

* You can specify a 3d shape, and some other parameters, and the program will display a 2d image of the shape from a particular position, without perception warping.
* You can give a color to a polygon, and the projection will shade them according to a light source, but only a _single_ light source
* 3d models can be stored in standardize format.
* ONLY allow triangular polygons. I don't know how to get Clojure2d to draw shapes with an arbitrary number of vertices, and figuring that out isn't the goal here.
* No generalizing projection from higher dimensions (though I think that might be pretty easy).

# Part 1: A DSL for drawing 2d images

First we need a simple program to define and draw 2d images using Clojure2d.

The ideal is to have a simple function `display: image` which has the side effect of drawing the image to the screen.

The specification of image I've started with is:

```clojure
;; image spec
{:image-size [1000 1000]
 :canvas-size {:x-max 100
               :x-min 0
               :y-max 100
               :y-min 0}
 :triangles [,,,]}

;; triangle spec
{:vertices #{[1 2] [3 4] [5 6]} :color [123 123 123]}
```

`canvas-size` is the size of the image using the coordinate system of the shapes. Values can be positive or negative.

`image-size` defines the size of the image in pixels. One constraint I haven't implemented is that the coordinate representation in pixels should be square. That is, the size in pixels of one grid in the x direction is the same as one grid in the y direction. The reason I've held off on this is because I'm not sure yet if this is actually _true_ when projecting from 3d. Your perspective obviously can lead to things being 'stretched'. But should this be handled here or in the projection mechanism? Not sure yet.

A `triangle` is a set of three 2d vectors and a color in `[r g b (a)]` format.

The code to implement `display` is fortunately pretty trivial:

```clojure
;; We need a way to navigate between the coordinates of the triangles and the pixels of the screen.
(defn- coord->px [{:keys [image-size canvas-size]} [x y]]
  (let [{:keys [x-max x-min y-max y-min]} canvas-size
        [x-image y-image] image-size]
    [(* (/ x-image (- x-max x-min)) (- x x-min))
     (- y-image (* (/ y-image (- y-max y-min)) (- y y-min)))]))

(defn- draw-triangle [canvas image {:keys [vertices color]}]
  (c2d/with-canvas [c canvas]
    (let [[[x1 y1] [x2 y2] [x3 y3]] (map #(coord->px image %) vertices)]
      (c2d/set-color c color)
      (c2d/triangle c x1 y1 x2 y2 x3 y3)
      (c2d/set-color c :black)
      (c2d/triangle c x1 y1 x2 y2 x3 y3 true))))

(defn display [image]
  (let [[x-image y-image] (:image-size image)
        canvas (c2d/canvas x-image y-image)]
    (doseq [t (:triangles image)] (draw-triangle canvas image t))
    (c2d/show-window canvas "Drawing")))

(comment
  ;; usage
  (display {:image-size [1000 1000]
            :canvas-size {:x-max 100
                          :x-min -100
                          :y-max 100
                          :y-min -100}
            :triangles #{{:vertices [[-50 -75] [-30 60] [50 0]] 
                          :color [155 155 155]}
                         {:vertices [[-25 -16] [-22 40] [100 15]] 
                          :color [50 50 50]}}}))
```

Now that's out of the way let's move to the actual projection mechanics.

# Projection Fundamentals

In essence, we have a set of 3d vectors we need to transform into a set of 2d vectors. This is a linear map: $M \cdot \vec{v} = \vec{w}$, where $M$ is a 2x3 matrix, $\vec{v}$ is a 3d vector, and $\vec{w}$ is a 2d vector.

The question is, what is the appropriate matrix to use?

The question, clearly, is the perspective of the viewer. If the viewer is 'at' coordinates $(a,b,c)$ and looking in direction $(d,e,f)$, that is going to impact how the image 'looks' in the 2d plane.

Let's encapsulate this idea of a viewer into a **camera**. A camera is defined with 2 vectors: the first stating its position, the second stating the direction it is looking in. There are probably other important things that need to be in here, like field of view, but I'll ignore that for now.


