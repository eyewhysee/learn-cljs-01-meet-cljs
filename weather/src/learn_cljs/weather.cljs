(ns ^:figwheel-hooks learn-cljs.weather
  (:require
   [goog.dom :as gdom]
   [reagent.dom :as rdom]
   [reagent.core :as r]))

(defn hello-world []
  [:div
   [:h1 {:class "app-title"} "Hello, World"]])

(defn mount-app-element []
  (rdom/render [hello-world] (gdom/getElement "app")))
(mount-app-element)

(defn ^:after-load on-reload []
  (mount-app-element))