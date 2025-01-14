(ns ymlgen.readers
  (:require [aero.core :as aero]))

(defmethod aero/reader 'ymlgen/var
  [opts _ value]
  (when-not (keyword? value)
    (throw (ex-info "The argument of #ymlgen/var should be a keyword."
                    {:variable value})))
  (let [variables (:variables opts)]
    (if-let [result (get variables value)]
      result
      (throw (ex-info (format "Variable %s not found" value) {})))))

(defmethod aero/reader 'ymlgen/include
  [{:keys [resolver source] :as opts} _ value]
  (let [path (:path value)
        variables (:variables value {})
        profile (:profile value)]
    (aero/read-config
     (if (map? resolver)
       (get resolver path)
       (resolver source path))
     (cond-> (update opts :variables merge variables)
       profile (assoc :profile profile)))))
