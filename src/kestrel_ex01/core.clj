(ns kestrel-ex01.core
  (:use [clojure.contrib.def :only [defn-memo]])
  (:import
   (net.spy.memcached MemcachedClient)
   (java.net InetSocketAddress)))

(defn- create-client
  [& {:keys [host port]
      :or {host "localhost" port 22133}}]
  (MemcachedClient. (list (InetSocketAddress. host port))))

(defn-memo default-client
    [& {:keys [host port]
      :or {host "localhost" port 22133}}]
    (create-client host port))

(defn set-item
  [queue-name data & {:keys [timeout] :or {timeout 0}}]
  (.set (default-client) queue-name timeout (str data)))

(defn decorate-queue-name
  [queue-name timeout mode0 mode1]
  (str queue-name
       (if timeout (str "/t=" timeout))
       (if mode0 (str "/" (name mode0)))
       (if mode1 (str "/" (name mode1)))))

(defn get-item
  [queue-name & {:keys [timeout mode0 mode1] }]
  (.get (default-client) (decorate-queue-name queue-name timeout mode0 mode1)))
