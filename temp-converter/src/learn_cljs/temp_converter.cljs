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
  ((c->k (f->c deg-f))))

(defn c->f [deg-c]
  (+ (* deg-c 1.8) 32))

(defn k->f [deg-k]
  (c->f (k->c deg-k)))

(def celsius-radio (gdom/getElement "unit-c"))              ;; <3>
(def fahrenheit-radio (gdom/getElement "unit-f"))
(def kelvin-radio (gdom/getElement "unit-k"))
(def temp-input (gdom/getElement "temp"))
(def output-target (gdom/getElement "temp-out"))
(def output-unit-target (gdom/getElement "unit-out"))
(def reset-button (gdom/getElement "reset-temp"))

(defn get-input-unit []                                     ;; <4>
  (if (.-checked celsius-radio)
    :celsius
    (if (.-checked fahrenheit-radio)
      :fahrenheit
      :kelvin)))

(defn get-input-temp []
  (js/parseInt (.-value temp-input)))

(defn set-output-temp [temp]
  (gdom/setTextContent output-target
                       (.toFixed temp 2)))

(defn update-output-OLD [_]                                    ;; <5>
  (if (= :celsius (get-input-unit))
    (do (set-output-temp (c->f (get-input-temp)))
        (gdom/setTextContent output-unit-target "F"))
    (do (set-output-temp (f->c (get-input-temp)))
        (gdom/setTextContent output-unit-target "C"))))

(defn update-output [_]
  (let [previous-unit (gdom/getTextConent output-unit-target)]
    (cond
      (and (= :celsius (get-input-unit)) (= "F" previous-unit))
      (do (set-output-temp (f->c (get-input-temp)))
          (gdom/setTextContent output-unit-target "C"))
      
      (and (= :celsius (get-input-unit)) (= "K" previous-unit))
      (do (set-output-temp (k->c (get-input-temp)))
          (gdom/setTextContent output-unit-target "C"))
      
      (and (= :fahrenheit (get-input-unit)) (= "C" previous-unit))
      (do (set-output-temp (c->f (get-input-temp)))
          (gdom/setTextContent output-unit-target "F"))
      
      (and (= :fahrenheit (get-input-unit)) (= "K" previous-unit))
      (do (set-output-temp (k->f (get-input-temp)))
          (gdom/setTextContent output-unit-target "F"))
      
      (and (= :kelvin (get-input-unit)) (= "C" previous-unit))
      (do (set-output-temp (c->k (get-input-temp)))
          (gdom/setTextContent output-unit-target "K"))
      
      (and (= :kelvin (get-input-unit)) (= "F" previous-unit))
      (do (set-output-temp (f->k (get-input-temp)))
          (gdom/setTextContent output-unit-target "K")))))

(defn reset-temp []
  (do
    (forms/setValue temp-input 0)
    (update-output nil)))

(gevents/listen temp-input "keyup" update-output)          ;; <6>
(gevents/listen celsius-radio "click" update-output)
(gevents/listen fahrenheit-radio "click" update-output)
(gevents/listen kelvin-radio "click" update-output)
(gevents/listen reset-button "click" reset-temp)
