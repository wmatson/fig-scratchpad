(ns fig-scratchpad.core
  (:require [reagent.core :as reagent :refer [atom]]
            [re-com.core :as re-com]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"
                          :lbs 170
                          :feet 5.5
                          :age 24
                          :resting-hr 70.0
                          :target-percent 0.7}))

(defn lbs->kg [lbs]
  (* 0.453592 lbs))

(defn feet->m [feet]
  (* 0.3048 feet))

(defn bmi [height-meters weight-kg]
  (/ weight-kg
     (* height-meters height-meters)))

(defn target-hr [max-hr resting-hr intensity]
  (+ resting-hr (* intensity (- max-hr resting-hr))))

  
(def lbs-cursor (reagent/cursor app-state [:lbs]))
(def height-cursor (reagent/cursor app-state [:feet]))
(def age-cursor (reagent/cursor app-state [:age]))
(def resting-hr-cursor (reagent/cursor app-state [:resting-hr]))
(def target-percent-cursor (reagent/cursor app-state [:target-percent]))

(defn re-calc-feet! [ft inches]
  (reset! height-cursor (+ ft (/ inches 12))))

(defn with-label [label body]
  (let [uuid (random-uuid)]
    [re-com/v-box
     :children [[:label {:for uuid} label]
                (vec (concat body [:attr {:id uuid}]))]]))

(defn input [placeholder cursor]
  (with-label placeholder
    [re-com/input-text :attr {:id uuid}
     :model (str @cursor)
     :on-change #(reset! cursor %)
     :placeholder placeholder]))

(defn bmi-display []
  [:div
   "BMI: "
   (let [height (feet->m @height-cursor)
         weight (lbs->kg @lbs-cursor)
         bmi (bmi height weight)]
     [:span (str bmi)])])

(defn max-heart-rate-display []
  [:div
   "Max Heart Rate:"
   [:span (str (- 220 @age-cursor))]])

(defn target-heart-rate-display [intensity]
  [:div
   (str (int (* 100 @intensity)) "% Target Heart Rate:")
   (let [max-hr (- 220 @age-cursor)
         resting-hr (* 1 @resting-hr-cursor)]
     [:span (str (int (target-hr max-hr resting-hr @intensity)))])])

(defn hello-world []
  [:div.container-fluid
   [:form
    [:div.form-row
     [input "Weight (lbs)" lbs-cursor]
     [input "Height (feet)" height-cursor]
     [input "Age (years)" age-cursor]
     [input "Resting Heart Rate (bpm)" resting-hr-cursor]]]
   [bmi-display]
   [max-heart-rate-display]
   [re-com/slider :min 0.3 :max 1.0 :step 0.01
    :model target-percent-cursor :on-change #(reset! target-percent-cursor %)]
   [target-heart-rate-display target-percent-cursor]])

(reagent/render-component (hello-world)
                          (. js/document (getElementById "app")))

(defn on-js-reload [])
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)

