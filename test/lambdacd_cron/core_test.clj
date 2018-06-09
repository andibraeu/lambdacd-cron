(ns lambdacd-cron.core-test
  (:use [lambdacd-cron.core]
        [lambdacd-cron.test-util]
        [lambdacd.steps.control-flow])
  (:require [clojure.test :refer :all])
  (:import (java.time ZonedDateTime))
  (:refer-clojure :exclude
                  [alias]))


(defn- super-now [dates]
  (let [state (atom dates)]
    (fn []
      (let [date (first @state)]
        (if (> (count @state) 1)
          (swap! state rest))
        date))))

(deftest cron-test
  (testing "that cron returns a step"
           (fn? (cron "0 0 * * *")))

  (testing "that cron get's killed if the step get's killed"
           (let [ctx        (some-ctx)
                 waiting-ch (start-waiting-for ((cron "0 0 * * *") {} ctx))]
             (reset! (:is-killed ctx) true)
             (is (= {:status :killed} (get-or-timeout waiting-ch :timeout 1500)))))

  (testing "that cron does not match the very same date it is started to prevent multiple pipelines starting"
           (with-redefs [now (super-now
                              [(ZonedDateTime/parse "2016-05-02T12:06:00+01:00[Europe/Paris]")])]
             (let [ctx        (some-ctx)
                   waiting-ch (start-waiting-for ((cron "6 * * * *") {} ctx))]
               (is (= {:status :timeout} (get-or-timeout waiting-ch :timeout 1500))))))

  (testing "that cron succeeds upon matching pattern"
           (with-redefs [now (super-now
                              [(ZonedDateTime/parse "2015-05-02T12:06:00+02:00[Europe/Berlin]")
                               (ZonedDateTime/parse "2015-05-02T12:07:00+02:00[Europe/Berlin]")])]
             (let [ctx        (some-ctx)
                   waiting-ch (start-waiting-for ((cron "7 * * * *") {} ctx))]
               (is (= {:status :success} (get-or-timeout waiting-ch :timeout 5500)))))))