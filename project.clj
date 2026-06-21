(defproject re-graph "0.2.1-SNAPSHOT"
  :description "GraphQL client for re-frame applications"
  :url "https://github.com/oliyh/re-graph"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  ;; lacinia (integration test server) pulls com.rpl/specter, whose recent
  ;; releases live in Red Planet Labs' Maven repo rather than Central.
  :repositories [["redplanetlabs" "https://nexus.redplanetlabs.com/repository/maven-public-releases"]]
  :plugins [[fundingcircle/lein-modules "0.3.15"]]
  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["modules" "change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag"]
                  ["install"]
                  ["deploy"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["modules" "change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]]
  :dependencies [[re-frame "1.4.7"]
                 [cljs-http "0.1.48"]
                 [org.clojure/tools.logging "1.3.0"]
                 [cheshire "5.13.0"]
                 [org.clojure/spec.alpha "0.5.238"]
                 [re-graph.hato :version]]
  :profiles {:provided {:dependencies [[org.clojure/clojure "1.12.1"]
                                       [org.clojure/clojurescript "1.12.42"]]}
             ;; ClojureScript tooling (figwheel-main pulls Jetty 9.x for its
             ;; dev server, so it must not share a classpath with the Pedestal
             ;; integration server, which is on Jetty 11+ - see :integration).
             :dev      {:source-paths   ["dev" "hato/src"]
                        :resource-paths ["dev-resources" "target"]
                        :clean-targets ^{:protect false} ["target"]
                        :dependencies   [[org.clojure/tools.reader "1.4.2"]
                                         [binaryage/devtools "1.0.7"]
                                         [day8.re-frame/test "0.1.7"]
                                         [com.bhauman/figwheel-main "0.2.20"]]
                        :repl-options   {:init-ns user}}

             ;; CLJ integration-test server: Pedestal 0.7 (Jetty 11+) + Lacinia.
             ;; hato (the default HTTP/WS client) arrives via the re-graph.hato
             ;; dependency. Kept separate from :dev to avoid a Jetty clash.
             :integration {:source-paths ["hato/src"]
                           :dependencies [[day8.re-frame/test "0.1.7"]
                                          [org.clojure/tools.reader "1.4.2"]
                                          ;; core-test exercises the clj-http error path too
                                          [clj-http "3.13.0"]
                                          [io.pedestal/pedestal.service "0.7.2"]
                                          [io.pedestal/pedestal.jetty "0.7.2"]
                                          [io.pedestal/pedestal.service-tools "0.7.2"]
                                          [com.walmartlabs/lacinia-pedestal "1.3.1"]
                                          [ch.qos.logback/logback-classic "1.5.6"]
                                          [org.slf4j/jul-to-slf4j "2.0.13"]
                                          [org.slf4j/jcl-over-slf4j "2.0.13"]
                                          [org.slf4j/log4j-over-slf4j "2.0.13"]]}

             :clj-http-gniazdo {:source-paths ["clj-http-gniazdo/src"]
                                :dependencies [[clj-http "3.13.0"]
                                               [stylefruits/gniazdo "1.2.2"]]}}
  :aliases {"fig"       ["trampoline" "run" "-m" "figwheel.main"]
            "fig:build" ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]
            "fig:min"   ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "dist"]
            "fig:test"  ["test-cljs"]
            ;; CLJ tests run under :integration (Jetty 11+); name the namespaces
            ;; explicitly so the figwheel-based cljs runner is never loaded here.
            "test-clj"  ["with-profile" "-dev,+integration" "test"
                         "re-graph.core-test"
                         "re-graph.internals-test"
                         "re-graph.core-deprecated-test"
                         "re-graph.integration-test"
                         "re-graph.deprecated-integration-test"]
            "test-cljs" ["with-profile" "+dev" "run" "-m" "re-graph.test-runner"]
            "test-all" ["do" ["clean"] ["test-clj"] ["test-cljs"]]
            "install" ["do" ["modules" "install"] ["install"]]
            "deploy" ["do" ["install"] ["deploy" "clojars"] ["modules" "deploy" "clojars"]]})
