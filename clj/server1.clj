(ns we)

(start-atom-db "/home/avital/swank/db/1/")

(run-server {:port 31337}
  "/*" (servlet server))
