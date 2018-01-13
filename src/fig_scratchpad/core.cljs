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
  [:div
   [:span placeholder]
   [:input {:placeholder placeholder
            :type "number"
            :value (rum/react cursor)
            :on-change (on-change-swap cursor)}]])

(rum/defc bmi-calc < rum/reactive []
  [:div
   "BMI: "
   (let [height (feet->m (rum/react height-cursor))
         bmi (/ (lbs->kg (rum/react lbs-cursor))
                (* height height))]
     [:span (str bmi)])])

(rum/defc max-heart-rate-calc < rum/reactive []
  [:div
   "Max Heart Rate:"
   [:span (str (- 220 (rum/react age-cursor)))]])

(rum/defc target-heart-rate-calc < rum/reactive [intensity]
  [:div
   (str (* 100 intensity) "% Target Heart Rate:")
   (let [max-hr (- 220 (rum/react age-cursor))
         resting-hr (* 1 (rum/react resting-hr-cursor))]
     [:span (str (+ resting-hr (* intensity (- max-hr resting-hr))))])])

  
(rum/defc hello-world []
  [:div
   (input "Weight (lbs)" lbs-cursor)
   (input "Height (feet)" height-cursor)
   (input "Age (years)" age-cursor)
   (input "Resting Heart Rate (bpm)" resting-hr-cursor)
   (bmi-calc)
   (max-heart-rate-calc)
   (target-heart-rate-calc 1)
   (target-heart-rate-calc 0.70)
   (target-heart-rate-calc 0.40)])

(rum/mount (hello-world)
           (. js/document (getElementById "app")))

(defn on-js-reload [])
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)

