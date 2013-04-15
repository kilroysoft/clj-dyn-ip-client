(ns clj-dyn-ip-client.sync
  (:use [clojure.java.shell]))

(defn router
  [path]
  (let [addr (sh "ping" "-r" "1" "-n" "1" path)
        addr (clojure.string/split-lines (:out addr))
        addr (nth addr 3)
        addr (first (re-find #"(\d+\.\d+.\d+\.\d+)" addr))]
    addr))

(router "kilroysoft.ch")
