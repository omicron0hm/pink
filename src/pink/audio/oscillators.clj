(ns pink.audio.oscillators
  "Oscillator Functions"
  (:require [pink.audio.engine :refer [*sr*]]
            [pink.audio.util :refer [create-buffer fill map-d swapd! setd! getd arg]]))

(def ^:const PI Math/PI)

(defn- ^double dec-if [^double a] (if (> a 1) (dec a) a))

(defn phasor 
  "Phasor with fixed frequency and starting phase"
  [^double freq ^double phase]
  (let [phase-incr ^double (/ freq  *sr*)
        cur-phase (double-array 1 phase)
        out (create-buffer)]
      (fn ^doubles [] 
        (fill out cur-phase #(dec-if (+ phase-incr ^double %))))))

(defn sine 
  "Sine generator with fixed frequency and starting phase"
  ([^double freq]
   (sine freq 0.0))
  ([^double freq ^double phase]
   (let [phsr (phasor freq phase)
         out (create-buffer)]
     (fn ^doubles []
       (map-d #(Math/sin (* 2.0 PI ^double %)) (phsr) out)))))


(defn vphasor [freq phase]
  "Phasor with variable frequency and phase (where freq and phase are generator
  functions"
  (let [out ^doubles (create-buffer)
        cur-phase (double-array 1 0)
        len (alength ^doubles out)
        lastindx (dec len)]
    (fn ^doubles [] 
      (let [f (freq)
            p (phase)]
        (when (and f p)
          (loop [cnt (unchecked-long 0)]
            (if (< cnt len)
              (let [incr ^double (/ (aget ^doubles f cnt) *sr*)
                    phs-adj (aget ^doubles p cnt)
                    phs-func (fn [^double c] ^double (+ c incr phs-adj))] 
                (aset out cnt (setd! cur-phase (dec-if (phs-func (getd cur-phase)))))
                (recur (unchecked-inc cnt)))
              out)))))))

(defn sine2 
  "Sine generator with variable frequency and phase (where freq and phase are
  generator functions"
  ([f]
   (sine2 f 0))
  ([f p]
   (let [phsr (vphasor (arg f) (arg p))
         out (create-buffer)]
     (fn ^doubles []
       (map-d #(Math/sin (* 2.0 PI ^double %)) (phsr) out)))))



