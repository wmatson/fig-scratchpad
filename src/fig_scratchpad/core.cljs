(ns fig-scratchpad.core
    (:require [rum.core :as rum]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"
                          :lbs 170
                          :feet 5.5
                          :age 24
                          :resting-hr 70.0}))

(defn lbs->kg [lbs]
  (* 0.453592 lbs))

(defn feet->m [feet]
  (* 0.3048 feet))

(defn bmi [height-meters weight-kg]
  (/ weight-kg
     (* height-meters height-meters)))

(defn target-hr [max-hr resting-hr intensity]
  (+ resting-hr (* intensity (- max-hr resting-hr))))
  
(def lbs-cursor (rum/cursor app-state :lbs))
(def height-cursor (rum/cursor app-state :feet))
(def age-cursor (rum/cursor app-state :age))
(def resting-hr-cursor (rum/cursor app-state :resting-hr))

(defn on-change-swap [cursor]
  (fn [e]
    (swap! cursor
           (fn [_]
             (.. e -target -value)))))

(rum/defc input < rum/reactive
  [placeholder cursor]
  (let [uuid (random-uuid)]
    [:div.form-group.col-auto
     [:label.col-form-label {:for uuid} placeholder]
     [:input.form-control.form-control-lg
      {:id uuid
       :placeholder placeholder
       :type "number"
       :value (rum/react cursor)
       :on-change (on-change-swap cursor)}]]))

(rum/defc bmi-display < rum/reactive []
  [:div
   "BMI: "
   (let [height (feet->m (rum/react height-cursor))
         weight (lbs->kg (rum/react lbs-cursor))
         bmi (bmi height weight)]
     [:span (str bmi)])])

(rum/defc max-heart-rate-display < rum/reactive []
  [:div
   "Max Heart Rate:"
   [:span (str (- 220 (rum/react age-cursor)))]])

(rum/defc target-heart-rate-display < rum/reactive [intensity]
  [:div
   (str (* 100 intensity) "% Target Heart Rate:")
   (let [max-hr (- 220 (rum/react age-cursor))
         resting-hr (* 1 (rum/react resting-hr-cursor))]
     [:span (str (target-hr max-hr resting-hr intensity))])])

(rum/defc hello-world []
  [:div.container-fluid
   [:form
    [:div.form-row
     (input "Weight (lbs)" lbs-cursor)
     (input "Height (feet)" height-cursor)
     (input "Age (years)" age-cursor)
     (input "Resting Heart Rate (bpm)" resting-hr-cursor)]]
   (bmi-display)
   (max-heart-rate-display)
   (target-heart-rate-display 1)
   (target-heart-rate-display 0.70)
   (target-heart-rate-display 0.40)])

(rum/mount (hello-world)
           (. js/document (getElementById "app")))

(defn on-js-reload [])
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)

