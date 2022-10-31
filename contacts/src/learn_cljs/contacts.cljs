(ns learn-cljs.contacts
  (:require-macros [hiccups.core :as hiccups])
  (:require [hiccups.runtime]
            [goog.dom :as gdom]
            [goog.events :as gevents]
            [clojure.string :as str]))

;; Models and State

(def contact-list [])

(defn make-address [address]
  (select-keys address [:street :city :state :postal :country]))

(defn maybe-set-address [contact]
  (if (:address contact)
    (update contact :address make-address)
    contact))

(defn make-contact [contact]
  (-> contact
      (select-keys [:first-name :last-name :email :phone :address])
      (maybe-set-address)))

(defn add-contact [contact-list input]
  (conj contact-list
        (make-contact input)))

(defn replace-contact [contact-list idx input]
  (assoc contact-list idx (make-contact input)))

(defn remove-contact [contact-list idx]
  (vec (concat (subvec contact-list 0 idx)
               (subvec contact-list (inc idx)))))

;; UI

(def app-container (gdom/getElement "app"))

(def top-bar
  [:div {:class "navbar has-shadow"}
   [:div {:class "container"}
    [:div {:class "navbar-brand"}
     [:span {:class "navbar-item"}
      "ClojureScript Contacts"]]]])

(defn action-button [id text icon-class]
  [:button {:id id
            :class "button is-primary is-light"}
   [:span {:class (str "mu " icon-class)}]
   (str " " text)])

(def save-button (action-button "save-contact" "Save" "mu-file"))
(def cancel-button (action-button "cancel-edit" "Cancel" "mu-cancel"))
(def add-button (action-button "add-contact" "Add" "mu-plus"))

(defn section-header [editing?]
  [:div {:class "section-header"}
   [:div {:class "level"}
    [:div {:class "level-left"}
     [:div {:class "level-item"}
      [:h1 {:class "subtitle"}
       [:span {:class "mu mu-user"}]
       "Edit Contact"]]]
    [:div {:class "level-right"}
     (if editing?
       [:div {:class "buttons"} cancel-button save-button]
       add-button)]]])

(defn format-name [contact]
  (->> contact
       ((juxt :first-name :last-name))
       (str/join " ")))

(defn delete-icon [idx]
  [:span {:class "delete-icon"
          :data-idx idx}
   [:span {:class "mu mu-delete"}]])

(defn render-contact-list-item [idx contact selected?]
  [:div {:class (str "card contact-summary" (when selected? " selected"))
         :data-idx idx}
   [:div {:class "card-content"}
    [:div {:class "level"}
     [:div {:class "level-left"}
      [:div {:class "level-item"}
       (delete-icon idx)
       (format-name contact)]]
     [:div {:class "level-right"}
      [:span {:class "mu mu-right"}]]]]])

(defn render-contact-list [state]
  (let [contacts (:contacts state)
        selected (:selected state)]
    [:div {:class "contact-list column is-4 hero is-fullheight"}
     (map-indexed (fn [idx contact]
                    (render-contact-list-item idx contact (= idx selected)))
                  contacts)]))

(def no-contact-details
  [:p {:class "notice"}
   "No contact selected"])

(defn form-field
  ([id value label] (form-field id value label "text"))
  ([id value label type]
   [:div {:class "field"}
    [:label {:class "label"} label]
    [:div {:class "control"}
     [:input {:id id
              :value value
              :type type
              :class "input"}]]]))

(defn render-contact-details [contact]
  (let [address (get contact :address {})]
    [:div {:id "contact-form" :class "contact-form"}
     (form-field "input-first-name" (:first-name contact) "First Name")
     (form-field "input-last-name" (:last-name contact) "Last Name")
     (form-field "input-email" (:email contact) "Email" "email")
     (form-field "input-phone" (:phone contact) "Phone number" "phone number")
     [:fieldset
      [:legend "Address"]
      (form-field "input-street" (:street address) "Street")
      (form-field "input-city" (:city address) "City")
      (form-field "input-state" (:state address) "State")
      (form-field "input-postal" (:postal address) "Postal Code")
      (form-field "input-country" (:country address) "Country")]]))

(defn get-field-value [id]
  (let [value (.-value (gdom/getElement id))]
    (when (seq value) value)))

(defn get-contact-form-data []
  {:first-name (get-field-value "input-first-name")
   :last-name (get-field-value "input-last-name")
   :email (get-field-value "input-email")
   :phone (get-field-value "input-phone")
   :address {:street (get-field-value "input-street")
             :city (get-field-value "input-city")
             :state (get-field-value "input-state")
             :postal (get-field-value "input-postal")
             :country (get-field-value "input-country")}})

(defn set-app-html! [html-str]
  (set! (.-innerHTML app-container) html-str))

(defn on-add-contact-handler [state]
  (-> state
      (assoc :editing? true)
      (dissoc :selected)))

(defn on-add-contact [state]
  (swap! state on-add-contact-handler))

(defn on-save-contact-handler [state]
  (let [contact (get-contact-form-data)
        idx (:selected state)
        state (dissoc state :selected :editing?)]
    (if idx
      (update state :contacts replace-contact idx contact)
      (update state :contacts add-contact contact))))

(defn on-save-contact [state]
  (swap! state on-save-contact-handler))

(defn on-cancel-edit [state]
  (swap! state dissoc :selected :editing?))

(defn on-open-contact-handler [state e]
  (let [idx (int (.. e -currentTarget -dataset -idx))]
    (assoc state :selected idx
           :editing? true)))

(defn on-open-contact [e state]
  (swap! state on-open-contact-handler e))

(defn on-delete-contact-handler [state e]
  (let [idx (int (.. e -currentTarget -dataset -idx))]
    (-> state
        (update :contacts remove-contact idx)
        (cond-> (= idx (:selected state))
          (dissoc :selected :editing?)))))

(defn on-delete-contact [e state]
  (.stopPropagation e)
  (swap! state on-delete-contact-handler e))

(defn attach-event-handlers! [state]
  (when-let [add-button (gdom/getElement "add-contact")]
    (gevents/listen add-button "click"
                    (fn [_] (on-add-contact state))))

  (when-let [save-button (gdom/getElement "save-contact")]
    (gevents/listen save-button "click"
                    (fn [_] (on-save-contact state))))

  (when-let [cancel-button (gdom/getElement "cancel-edit")]
    (gevents/listen cancel-button "click"
                    (fn [_] (on-cancel-edit state))))

  (doseq [elem (array-seq (gdom/getElementsByClass "contact-summary"))]
    (gevents/listen elem "click"
                    (fn [e] (on-open-contact e state))))

  (doseq [elem (array-seq (gdom/getElementsByClass "delete-icon"))]
    (gevents/listen elem "click"
                    (fn [e] (on-delete-contact e state)))))

(defn render-app! [state]
  (set-app-html!
   (hiccups/html
    [:div {:class "app-main"}
     top-bar
     [:div {:class "columns"}
      (render-contact-list state)
      [:div {:class "contact-details column is-8"}
       (section-header (:editing? state))
       [:div {:class "hero is-fullheight"}
        (if (:editing? state)
          (render-contact-details (get-in state [:contacts (:selected state)] {}))
          no-contact-details)]]]])))

(def initial-state
  {:contacts contact-list
   :selected nil
   :editing? false})

(defonce app-state (atom initial-state))

(defonce is-initialized?
  (do
    (add-watch app-state :state-observer
               (fn [_ atom _ new-val]
                 ; we want want to render new-val (the dereferenced value of the atom)
                 (render-app! new-val)
                 ; however, the event handlers need the atom itself so we can call swap!
                 (attach-event-handlers! atom)))
    ; we want to render the dereferenced value of the atom
    (render-app! @app-state)
    ; however, the event handlers need the atom itself so we can call swap!
    (attach-event-handlers! app-state)
    
    true))