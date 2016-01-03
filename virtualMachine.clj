(ns turing.virtualMachine)

(defn read-tuples [file-name]
  (->>
    (clojure.string/split-lines(slurp file-name))
    (map #(re-find #"\s*-?\d+\s+.\s+-?\d+\s+.\s+[r|l]\s*" %))
    (filter (fn [tuple] (not (nil? tuple))))
    (map #(clojure.string/split % #"\s+"))

    (map (fn 
           [[state, input, next-state, output, direction]]
           {[(read-string state) input] {:S (read-string state)
                                         :I input
                                         :NS (read-string next-state)
                                         :O output
                                         :D (if (= direction "r") 1 -1 )
                                         
                                         }}
           ))
    (reduce merge)
    ))
(defn trace-print [tape position]
  ;(print (clojure.string/join (subvec tape 0 position)) 
   ;      (subvec tape (- position 1) position) (clojure.string/join (subvec tape position)))
  )

(defn run [state tuples position tape]
  (if (contains? tuples [((tuples [state (tape position)]) :NS) 
      [tape (+ position ((tuples [state (tape position)]) :D))]])
    
    (run ((tuples [state (tape position)]):NS) tuples (+((tuples [state (tape position)]):D)
         ) (assoc tape position ((tuples [state (tape position)]):O)))
    tape
  )
  )

(defn trace-run [state tuples position tape]
  (print [((tuples [state (tape position)]) :NS) 
      (tape (+ position ((tuples [state (tape position)]) :D)))])
  
  (if (contains? tuples [((tuples [state (tape position)]) :NS) 
      (tape (+ position ((tuples [state (tape position)]) :D)))])
    
    (do (trace-print tape position)
      (trace-run ((tuples [state (tape position)]):NS) tuples (+((tuples [state (tape position)]):D) position)
                 (assoc tape position ((tuples [state (tape position)]):O))))
  )
  )

(defn set-up [state position tape file trace]
  (let [tuples (read-tuples file)  tape-array (subvec (clojure.string/split tape #"") 1)]
    (if trace
    (print (trace-run state tuples position tape-array))
    (print (run state tuples position tape-array))  
    ))
    )
  
  