(defproject lambdacd-cron "0.1.2-SNAPSHOT"
  :description "A cron for your lambdacd"
  :url "https://github.com/felixb/lambdacd-cron"
  :license {:name "Apache License, version 2.0"
            :url  "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [com.cronutils/cron-utils "7.0.2"]
                 [lambdacd "0.14.1"]]
  :test-paths ["test" "example"]
  :profiles {:dev {:main         lambdacd-cron.example.pipeline
                   :dependencies [[compojure "1.6.0"]
                                  [ring-server "0.4.0"]
                                  [ring/ring-mock "0.3.2"]]}})
