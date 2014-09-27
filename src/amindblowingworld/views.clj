(ns amindblowingworld.views
  (:require
    [amindblowingworld.world :refer :all]
    [amindblowingworld.civs :refer :all]
    [amindblowingworld.history :refer :all]
    [clojure.data.json :as json]))

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
         scale-factor 5
         img (BufferedImage. (* scale-factor w) (* scale-factor h) (BufferedImage/TYPE_INT_ARGB))
         g (.createGraphics img)
         b (-> world .getBiome)]
    (doseq [y (range h)]
      (doseq [x (range w)]
        (if (settlement-at {:x x :y y})
          (.setColor g (Color. 255 0 0))
          (let [pos {:x x :y y}
                biome (.get b x y)]
            (case (.name biome)
              "OCEAN"        (.setColor g (Color. 0 0 255))
              "ICELAND"      (.setColor g (Color. 255 255 255))
              "TUNDRA"       (.setColor g (Color. 141 227 218))
              "ALPINE"       (.setColor g (Color. 141 227 218))
              "GLACIER"      (.setColor g (Color. 255 255 255))
              "GRASSLAND"    (.setColor g (Color. 80 173 88))
              "ROCK_DESERT"  (.setColor g (Color. 105 120 59))
              "SAND_DESERT"  (.setColor g (Color. 205 227 141))
              "FOREST"       (.setColor g (Color. 59 120 64))
              "SAVANNA"      (.setColor g (Color. 171 161 27))
              "JUNGLE"       (.setColor g (Color. 5 227 34))
              (.setColor g (Color. 255 0 0)))))
          (let [pixel-x (* x scale-factor)
                pixel-y (* y scale-factor)]
            (.fillRect g pixel-x pixel-y scale-factor scale-factor))))
    (.dispose g)
    img))

(defn do-response-biome-map []
  (let [bm (biome-map (get-world))
        bytes (image-bytes bm "png")]
    (response-png-image-from-bytes bytes)))

(defn response-biome-map[]
  (time (do-response-biome-map)))

(defn- get-history-since [event-id]
  [(.length @events) (subvec @events event-id)])

(defn history-since [event-id]
  (json/write-str (get-history-since event-id)))

