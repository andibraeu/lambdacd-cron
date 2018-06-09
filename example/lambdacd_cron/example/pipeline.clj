(ns lambdacd-cron.example.pipeline
  (:use [compojure.core])
  (:require [lambdacd.steps.shell :as shell]
            [lambdacd.steps.manualtrigger :refer [wait-for-manual-trigger]]
            [lambdacd.steps.control-flow :refer [either with-workspace in-parallel run]]
            [lambdacd.core :as lambdacd]
            [ring.server.standalone :as ring-server]
            [lambdacd.ui.core :as ui]
            [lambdacd-cron.core :as lambdacd-cron]
            [lambdacd.runners :as runners]
            )
  (:import (java.nio.file.attribute FileAttribute)
           (java.nio.file Files LinkOption)))

(defn- create-temp-dir []
  (str (Files/createTempDirectory "crontrigger" (into-array FileAttribute []))))

(defn print-date [args ctx]
  (shell/bash ctx (:cwd args) "date"))

(def pipeline-structure
  `((either
      wait-for-manual-trigger
      (lambdacd-cron/cron "0 12 * * *")) ; trigger build every day at 12:00 UTC
     print-date))

(defn -main [& args]
  (let [home-dir (create-temp-dir)
        config {:home-dir home-dir}
        pipeline (lambdacd/assemble-pipeline pipeline-structure config)]
    (runners/start-one-run-after-another pipeline)
    (ring-server/serve (routes
                         (ui/ui-for pipeline))
                       {:open-browser? false
                        :port          8082})))