(ns ^:figwheel-hooks learn-cljs.weather
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]))

(println "This text is printed from src/learn_cljs/weather.cljs. Go ahead and edit it and see reloading in action.")

(defn multiply [a b] (* a b))

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Live reloading REALLY rocks!"}))

(defn get-app-element []
  (gdom/getElement "app"))

(defn hello-world []
  [:h1
   [:h1 "I say: " (:text @app-state)]
   [:h3 "Edit this in src/learn_cljs/weather.cljs and watch it change!"]])

; new component
(defn greeter []
  [:div
   [:h3 "Hello, from Learning ClojureScript Lesson 6"]])

(defn mount [el]
  (rdom/render [hello-world] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

; idempotent function example
(defn append-element [parent child]
  (when-not(.contains parent child)
   (.appendChild parent child)))

; defonce example with first use of do expression for wrapping initialization code
(defonce is-initialized?
  (do
    (.setItem js/localStorage "init-at" (.now js/Date))
    (js/alert "Welcome!")
    true))

; Display/Business Logic Separation
; his admittedly inefficient example
;(defonce messages (atom []))
;(defn receive-message [text timestamp]
;  (swap! messages conj {:text text :timestamp timestamp}))
;(defn render-all-messages! [messages]
;  (set! (.-innerHTML messages-feed) "")
;  (doseq [message @messages]
;    (let [node (.createElement js/document "div")]
;      (set! (.-innerHTML node) (str "[" timestamp "]: " text))
;      (.appendChild messages-feed node))))
; (render-all-messages!)

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^:after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
