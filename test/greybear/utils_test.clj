(ns greybear.utils-test
  (:use midje.sweet
        [greybear.utils]))

(fact "parse-int should parse a string into an integer"
      (parse-int "142") => 142
      (parse-int "-123") => -123)

(facts "about position"
  (fact "should return a position tuple"
    (position "1-3") => [1 3]
    (position "17-18") => [17 18])
  (fact "should raise an exception on invalid input"
    (position "19-3") => (throws AssertionError)))

(facts "about place-stone"
  (fact "changes stone at position to black"
    (place-stone '(\0 \0 \0 \0 \0 \0) "0-2" \1) => "001000")
  (fact "changes stone at position to white"
    (place-stone '(\0 \0 \0 \0 \0 \0) "0-2" \2) => "002000"))
