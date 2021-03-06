(ns pink.demo.demo-duration
 (:require [pink.simple :refer :all]
             [pink.event :refer :all] 
             [pink.space :refer [pan]] 
             [pink.oscillators :refer [blit-saw blit-square]]
             [pink.envelopes :refer [adsr]]
             [pink.util :refer :all]
             [pink.node :refer :all]
             [pink.filters :refer [tone butterlp]]
             [pink.delays :refer [adelay]]
             [pink.config :refer [*duration*]]
             ))

(defn instr-saw
  [amp freq loc]
  (let-s [e (adsr 0.03 0.01 0.9 3.0)] 
    (pan 
      (mul e
           (butterlp (blit-saw freq) 
                 (sum 100 (mul e 400))))
      loc)))

(comment

  (start-engine)

  (add-afunc (instr-saw 0.5 440.0 0.0))

  (add-afunc
    (binding [*duration* 2.0]
      (instr-saw 0.5 440.0 0.0)))

  (add-afunc
    (with-duration 1.35
      (instr-saw 0.5 (+ 220.0 (* 440.0 (rand))) 0.0)))

  (stop-engine)


  )

