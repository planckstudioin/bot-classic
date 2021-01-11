<?php
require_once './includes/youtube.class.php';
$yt       = new youtube();
$responce = array();

if (isset($_REQUEST["url"])) {

    $url  = $_REQUEST["url"];
    $time = time();

    $res = $yt->media_info($url);

    if ($res['title'] != null) {
        $responce['code']    = "200";
        $responce['status']  = "success";
        $responce['message'] = "Youtube video found";
        $responce['api']     = "v1";
        $responce['time']    = $time;
        $responce['result']  = $res;
    }

} else {
    $responce['code']    = "401";
    $responce['status']  = "failed";
    $responce['message'] = "Invalid youtube url";
    $responce['api']     = "v1";
    $responce['time']    = $time;
    $responce['result']  = [];
}

header('Content-Type: application/json');
echo json_encode($responce, JSON_UNESCAPED_SLASHES);
