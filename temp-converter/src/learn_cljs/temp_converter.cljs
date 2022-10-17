(ns learn-cljs.temp-converter
  (:require [goog.dom :as gdom]                            ;; <1>
            [goog.dom.forms :as forms]
            [goog.events :as gevents]))

(defn f->c [deg-f]                                         ;; <2>
  (/ (- deg-f 32) 1.8))

(defn c->f [deg-c]
  (+ (* deg-c 1.8) 32))

(def celsius-radio (gdom/getElement "unit-c"))              ;; <3>
(def fahrenheit-radio (gdom/getElement "unit-f"))
(def temp-input (gdom/getElement "temp"))
(def output-target (gdom/getElement "temp-out"))
(def output-unit-target (gdom/getElement "unit-out"))
(def reset-button (gdom/getElement "reset-temp"))

(defn get-input-unit []                                     ;; <4>
  (if (.-checked celsius-radio)
    :celsius
    :fahrenheit))

(defn get-input-temp []
  (js/parseInt (.-value temp-input)))

(defn set-output-temp [temp]
  (gdom/setTextContent output-target
                       (.toFixed temp 2)))

(defn update-output [_]                                    ;; <5>
  (if (= :celsius (get-input-unit))
    (do (set-output-temp (c->f (get-input-temp)))
        (gdom/setTextContent output-unit-target "F"))
    (do (set-output-temp (f->c (get-input-temp)))
        (gdom/setTextContent output-unit-target "C"))))

(defn reset-temp []
  (do
    (forms/setValue temp-input 0)
    (update-output nil)))

(gevents/listen temp-input "keyup" update-output)          ;; <6>
(gevents/listen celsius-radio "click" update-output)
(gevents/listen fahrenheit-radio "click" update-output)
(gevents/listen reset-button "click" reset-temp)
