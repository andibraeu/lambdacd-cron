(defproject lambdacd-cron "0.1.3-SNAPSHOT"
  :description "A cron for your lambdacd"
  :url "https://github.com/felixb/lambdacd-cron"
  :license {:name "Apache License, version 2.0"
            :url  "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [com.cronutils/cron-utils "8.0.0"]
                 [lambdacd "0.14.3"]]
  :test-paths ["test" "example"]
  :profiles {:dev {:main         lambdacd-cron.example.pipeline
                   :dependencies [[compojure "1.6.1"]
                                  [ring-server "0.5.0"]
                                  [ring/ring-mock "0.3.2"]]}})
