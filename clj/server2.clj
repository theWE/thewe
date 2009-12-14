(ns we)

(start-atom-db "/home/avital/swank/db/2/")

(run-server {:port 31338}
  "/*" (servlet server))
