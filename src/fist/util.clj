(ns fist.util
  (:require [noir.io :as io]
            [markdown.core :as md]
            [formative.core :as f]
            [hiccup.page :as page]))

(defn md->html
  "reads a markdown file from public/md and returns an HTML string"
  [filename]
  (->>
    (io/slurp-resource filename)
    (md/md-to-html-string)))

(defn render-field [field]
  (page/html5 (f/render-field field)))

(defn render-form-field [field-name form]
  (render-field (first (filter #(= (:name %) field-name) (:fields form)))))