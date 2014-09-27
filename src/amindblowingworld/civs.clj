(ns amindblowingworld.civs
  )

(import '(com.github.lands.World))
(import '(com.github.lands.Biome))

(defn load-world [filename]
  (let [f (java.io.File. filename)]
    (. com.github.lands.PickleSerialization loadWorld f)))

(def world (load-world "worlds/seed_13038.world"))

; --------------------------------------
; Records
; --------------------------------------

(defrecord Tribe [id name language settlements])
(defrecord Settlement [id name pop owner pos])
(defrecord Game [next-id tribes settlements])

; --------------------------------------
; Global state
; --------------------------------------

(def world (atom (load-world "worlds/seed_13038.world")))

(defn create-game []
  (Game. 1 {} {}))

(def game  (atom (create-game)))

; --------------------------------------
; Tribe functions
; --------------------------------------

(defn- create-tribe-in-game [game]
  (let [id-tribe (.next-id game)
        game (assoc game :next-id (inc id-tribe))
        id-settlement (.next-id game)
        game (assoc game :next-id (inc id-settlement))
        language nil
        name-tribe nil
        name-settlement nil
        pos {:x 0 :y 0}
        settlement (Settlement. id-settlement name-settlement 100 id-tribe pos)
        tribe (Tribe. id-tribe nil nil [id-settlement])
        game (assoc-in game [:tribes id-tribe] tribe)
        game (assoc-in game [:settlements id-settlement] settlement)]
      (println "Creating tribe " tribe)
      (println "Creating settlement " settlement)
    game))

(defn create-tribe []
  (swap! game create-tribe-in-game))

; --------------------------------------
; Game functions
; --------------------------------------

(defn total-pop []
  (reduce + (map :pop (vals (.settlements @game)))))

(defn pop-balancer []
  (let [pop (total-pop)]
    (println "Balancing population, total pop: " pop)
    (if (< pop 1000)
      (create-tribe)
      (println "...nothing to do"))))

(defn run-every-second [f]
  (future (Thread/sleep 1000)
    (f)
    (run-every-second f)))

(defn init []
  (run-every-second pop-balancer))

(defn biome-matrix [world]
  (let [w (-> world .getDimension .getWidth)
        h (-> world .getDimension .getHeight)
        b (-> world .getBiome)]
    (for [y (range h)]
      (for [x (range w)]
        (.get b x y)))))

