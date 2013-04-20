(ns clj-dyn-ip-client.sync
  (:use [clojure.java.shell])
   (:require [clj-http.client :as client]))


(def ^:dynamic *active-sync* (atom false))
(def ^:dynamic *router* (atom nil))
(def SERVER "kilroysoft.ch")
(def CHANGE "http://kilroysoft.ch:8080/am/changeam?host=%s;ip=%s")  
;(def CHANGE "http://kilroysoft.ch")  
    
(defn- router
  [path]
  (let [ping (sh "ping" "-r" "1" "-n" "1" path)]
    (if (= (:exit ping) 0)
      (first (re-find #"(\d+\.\d+.\d+\.\d+)" 
                      (nth 
                       (clojure.string/split-lines (:out ping)) 
                       3)))
      nil)))


(defn- host
  []
  (clojure.string/trim (:out (sh "hostname"))))

(defn- now [] (new java.util.Date))

(defn- send-server
  [ip host]
  (try
    (client/get (format 
                 CHANGE 
                 (str host) ip))
    ip
    (catch Exception e nil)))

(defn- loop-sync
  []
  (when @*active-sync*
    (when-let [ip (router SERVER)]
      ;(when (not= ip @*router*)
        (reset! *router* 
                (send-server ip (host)))
      );)
    (Thread/sleep 1000)
    (println (now) @*router*)
    (recur)))

(defn start
  []
  (reset! *active-sync* true)
  (.start (Thread. (loop-sync))))

(defn stop
  []
  (reset! *active-sync* false))

; (start)

; (stop)