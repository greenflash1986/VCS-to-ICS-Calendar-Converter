<?php
header('Content-Type: text/html; charset=UTF-8');
$str = "Hello=0Aworld.";
$jap1 = "=E3=91=81";
$jap2 = "=E3=91=B9";
$jap3 = "=E3=93=87";
$jap = "=E3=91=81=E3=91=B9=E3=93=87";

echo quoted_printable_decode($str . "<br>");
echo quoted_printable_decode($jap1 . "<br>");
echo quoted_printable_decode($jap2 . "<br>");
echo quoted_printable_decode($jap3 . "<br>");
echo quoted_printable_decode($jap . "<br>");

?>