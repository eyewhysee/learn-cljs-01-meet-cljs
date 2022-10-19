(ns learn-cljs.temp-converter
  (:require [goog.dom :as gdom]                            ;; <1>
            [goog.dom.forms :as forms]
            [goog.events :as gevents]))

(defn c->k [deg-c]
  (+ deg-c 273.15))

(defn k->c [deg-k]
  (- deg-k 273.15))

(defn f->c [deg-f]                                         ;; <2>
  (/ (- deg-f 32) 1.8))

(defn f->k [deg-f]
  (c->k (f->c deg-f)))

(defn c->f [deg-c]
  (+ (* deg-c 1.8) 32))

(defn k->f [deg-k]
  (c->f (k->c deg-k)))

(defn f->f [deg-f]
  deg-f)

(defn c->c [deg-c]
  deg-c)

(defn k->k [deg-k]
  deg-k)

(def from-celsius-radio (gdom/getElement "from-unit-c"))            ;; <3>
(def from-fahrenheit-radio (gdom/getElement "from-unit-f"))
(def from-kelvin-radio (gdom/getElement "from-unit-k"))
(def to-celsius-radio (gdom/getElement "to-unit-c"))            ;; <3>
(def to-fahrenheit-radio (gdom/getElement "to-unit-f"))
(def to-kelvin-radio (gdom/getElement "to-unit-k"))
(def temp-input (gdom/getElement "temp"))
(def output-target (gdom/getElement "temp-out"))
(def output-unit-target (gdom/getElement "unit-out"))
(def reset-button (gdom/getElement "reset-temp"))

(defn get-from-unit []                                     ;; <4>
  (if (.-checked from-celsius-radio)
    :celsius
    (if (.-checked from-fahrenheit-radio)
      :fahrenheit
      :kelvin)))

(defn get-to-unit []                                     ;; <4>
  (if (.-checked to-celsius-radio)
    :celsius
    (if (.-checked to-fahrenheit-radio)
      :fahrenheit
      :kelvin)))

(defn get-input-temp []
  (js/parseInt (.-value temp-input)))

(defn set-output-temp [temp]
  (gdom/setTextContent output-target
                       (.toFixed temp 2)))

(defn update-output [_](                                       ;; <5>
    (cond
      (and (= :fahrenheit (get-from-unit)) (= :celsius (get-to-unit)))
      (do (set-output-temp (f->c (get-input-temp)))
          (gdom/setTextContent output-unit-target "C"))
      
      (and (= :celsius (get-from-unit)) (= :celsius (get-to-unit)))
      (do (set-output-temp (c->c (get-input-temp)))
          (gdom/setTextContent output-unit-target "C"))
      
      (and (= :kelvin (get-from-unit)) (= :celsius (get-to-unit)))
      (do (set-output-temp (k->c (get-input-temp)))
          (gdom/setTextContent output-unit-target "C"))
      
      (and (= :fahrenheit (get-from-unit)) (= :fahrenheit (get-to-unit)))
      (do (set-output-temp (f->f (get-input-temp)))
          (gdom/setTextContent output-unit-target "F"))
      
      (and (= :celsius (get-from-unit)) (= :fahrenheit (get-to-unit)))
      (do (set-output-temp (c->f (get-input-temp)))
          (gdom/setTextContent output-unit-target "F"))
      
      (and (= :kelvin (get-from-unit)) (= :fahrenheit (get-to-unit)))
      (do (set-output-temp (k->f (get-input-temp)))
          (gdom/setTextContent output-unit-target "F"))
      
      (and (= :fahrenheit (get-from-unit)) (= :kelvin (get-to-unit)))
      (do (set-output-temp (f->k (get-input-temp)))
          (gdom/setTextContent output-unit-target "K"))
      
      (and (= :celsius (get-from-unit)) (= :kelvin (get-to-unit)))
      (do (set-output-temp (c->k (get-input-temp)))
          (gdom/setTextContent output-unit-target "K"))
      
      (and (= :kelvin (get-from-unit)) (= :kelvin (get-to-unit)))
      (do (set-output-temp (k->k (get-input-temp)))
          (gdom/setTextContent output-unit-target "K")))))
      

(defn reset-temp []
  (forms/setValue temp-input 0)
  (update-output 0))

(gevents/listen temp-input "keyup" update-output)          ;; <6>
(gevents/listen from-celsius-radio "click" update-output)
(gevents/listen from-fahrenheit-radio "click" update-output)
(gevents/listen from-kelvin-radio "click" update-output)
(gevents/listen to-celsius-radio "click" update-output)
(gevents/listen to-fahrenheit-radio "click" update-output)
(gevents/listen to-kelvin-radio "click" update-output)
(gevents/listen reset-button "click" reset-temp)
