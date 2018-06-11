(ns lambdacd-cron.core
  (:require [lambdacd.stepsupport.killable :as support]
            [clojure.core.async :as async])

  (:import
    (java.util Locale)
    (java.time ZonedDateTime)
    (java.time.temporal ChronoUnit)
    (com.cronutils.model.definition CronDefinitionBuilder)
    (com.cronutils.parser CronParser)
    (com.cronutils.descriptor CronDescriptor)
    (com.cronutils.model.time ExecutionTime)
    (com.cronutils.model CronType)))

(defn now[]
  (ZonedDateTime/now))

(defn- wait-for-cron-while-not-killed [ctx cron]
  (let [start-date (now)]
    (loop []
      (support/if-not-killed ctx
                             (let [current-date  (now)
                                   executionTime (ExecutionTime/forCron cron)
                                   startToNow    (.until start-date current-date ChronoUnit/SECONDS)]

                               (if (and (>= startToNow 60)
                                        (.isMatch executionTime current-date))
                                 {:status :success}
                                 (do
                                   (Thread/sleep (* 5 1000))
                                   (recur))))))))

(defn cron
  "Build step that waits for a default cron pattern to match"
  [pattern]
  (fn [_ ctx]
    (let [cron            (-> (CronDefinitionBuilder/instanceDefinitionFor CronType/UNIX)
                              (CronParser.)
                              (.parse pattern))
          description     (-> (CronDescriptor/instance Locale/US)
                              (.describe cron))
          result-ch       (:result-channel ctx)
          _               (async/>!! result-ch [:status :waiting])
          _               (async/>!! result-ch
                                     [:out
                                      (str "Waiting for cron with pattern '" pattern "' ("
                                           description ")")])
          wait-result     (wait-for-cron-while-not-killed ctx cron)]
      wait-result)))
