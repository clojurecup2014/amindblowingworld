(ns amindblowingworld.views
  (:require
    [hiccup
      [page :refer [html5]]
      [page :refer [include-js]]]
    [amindblowingworld.world :refer :all]))

; ----------------------------------------------------
; Images
; ----------------------------------------------------

(import java.io.ByteArrayOutputStream)
(import java.io.ByteArrayInputStream)
(import java.awt.image.BufferedImage)
(import java.awt.RenderingHints)
(import java.awt.Color)
(import javax.imageio.ImageIO)

(defn image-bytes [image format]
  (let [baos (ByteArrayOutputStream.)
        _ (ImageIO/write image format, baos )
        _ (.flush baos)
        bytes (.toByteArray baos)
        _ (.close baos)]
    bytes))

(defn response-png-image-from-bytes [bytes]
  {
    :status 200
    :headers {"Content-Type" "image/png"}
    :body (ByteArrayInputStream. bytes)
    })

(defn biome-map [world]
  (let [ w (-> world .getDimension .getWidth)
         h (-> world .getDimension .getHeight)
         img (BufferedImage. w h (BufferedImage/TYPE_INT_ARGB))
         g (.createGraphics img)
         b (-> world .getBiome)]
    (doseq [y (range h)]
      (doseq [x (range w)]
        (let [pos {:x x :y y}
              biome (.get b x y)]
          (case (.name biome)
            "OCEAN" (.setColor g (Color. 0 0 255))
            (.setColor g (Color. 255 0 0)))
            (.fillRect g x y 1 1))))
    (.dispose g)
    img))

(defn response-biome-map []
  (let [bm (biome-map world)
        bytes (image-bytes bm "png")]
    (response-png-image-from-bytes bytes)))

; ----------------------------------------------------
; Index
; ----------------------------------------------------

(defn index-page []
  (html5
    [:head
      [:title "AMindBlowingWorld"]
      (include-js "/js/main.js")
      (include-js "/js/app.js")]
    [:body
      [:h1 "AMindBlowingWorld"]
      [:div#authDiv "auth here"]
      [:div#appDiv
        [:div#world
          [:img#worldView {:src "/img/world.png"}]]]
    ]))
