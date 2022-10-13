(ns ^:figwheel-hooks learn-cljs.weather
  (:require
   [goog.dom :as gdom]
   [reagent.dom :as rdom]
   [reagent.core :as r]
   [ajax.core :as ajax])
    (:require-macros
     [adzerk.env :as env]))

; application state
(defonce app-state (r/atom {:title "WhichWeather"
                            :postal-code ""
                            :temperatures {:today {:label "Today"
                                                   :value nil}
                                           :tomorrow {:label "Tomorrow"
                                                      :value nil}}}))

; use environment variables instead of directly including secrets (securuity risk)
; https://gitpod.io/variables (search gitpod docs for "Environment Variables")
; https://stackoverflow.com/a/67865832
(env/def OPENWEATHERMAP_API_KEY "no default key available")

; OpenWeatherMap API key
(def api-key OPENWEATHERMAP_API_KEY)

; update app-state with forecast data
(defn handle-response [resp]
  (let [today (get-in resp ["list" 0 "main" "temp"])
        tomorrow (get-in resp ["list" 8 "main" "temp"])]
    (swap! app-state update-in [:temperatures :today :value] (constantly today))
    (swap! app-state update-in [:temperatures :tomorrow :value] (constantly tomorrow))))

; retrieve forecast data from OpenWeatherMap
(defn get-forecast! []
  (let [postal-code (:postal-code @app-state)]
    (ajax/GET "https://api.openweathermap.org/data/2.5/forecast"
      {:params {"q" postal-code
                "units" "imperial"
                "appid" api-key}
       :handler handle-response})))

; Reagent components
(defn title []
  [:h1 (:title @app-state)])

(defn temperature [temp]
  [:div {:class "temperature"}
   [:div {:class "value"}
    (:value temp)]
   [:h2 (:label temp)]])

(defn go-button []
  [:button {:on-click get-forecast!} "Get forecast!"])

(defn postal-code []
  [:div {:class "postal-code"}
   [:h3 "Enter your postal code"]
   [:input {:type "number"
            :placeholder "Postal Code"
            :value (:postal-code @app-state)
            :on-change #(swap! app-state assoc :postal-code (-> % .-target .-value))}]
   [go-button]])

(defn app []
  [:div {:class "app"}
   [title]
   [:div {:class "temperatures"}
    (for [temp (vals (:temperatures @app-state))]
      [temperature temp])]
   [postal-code]])

(defn mount-app-element []
  (rdom/render [app] (gdom/getElement "app")))
(mount-app-element)

; Instruct Figwheel to re-mount the app whenever reloading code
(defn ^:after-load on-reload []
  (mount-app-element))