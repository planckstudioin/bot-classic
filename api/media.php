<?php
header("Access-Control-Allow-Origin: *");

$responce = array();

if (isset($_REQUEST["url"])) {
    $link = $_REQUEST["url"]; 
    $curl = curl_init();
    
    curl_setopt_array($curl, [
        CURLOPT_RETURNTRANSFER => 1,
        CURLOPT_URL => $link.'?__a=1',
        CURLOPT_HTTPHEADER => [
            'Cookie: ig_did=DD442250-0440-40D2-B5E3-F49970BEDD01; mid=X41DwwALAAHxaxgQAheOIsme4k9H; ig_nrcb=1; csrftoken=5PIoYazrvSH0oHuwfEnqN35LakAb81XJ; ds_user_id=8173227811; sessionid=8173227811%3A3TJBtZDyUjhkye%3A5; shbid=17634; shbts=1603093503.691378; rur=PRN; urlgen="{\"2405:205:c821:8b42:8d77:9f7a:bc5f:2366\": 55836}:1kUPqx:3nqbBg8edyatZYt8c_nLSJdDN1w"',
        ],
        CURLOPT_SSL_VERIFYHOST => 0,
        CURLOPT_FOLLOWLOCATION => false,
        CURLOPT_USERAGENT => 'Mozilla/5.0 (Linux; Android 6.0.1; Moto G (4)) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Mobile Safari/537.36',
        CURLOPT_HEADER => false
    ]);

    $resp = curl_exec($curl);
    curl_close($curl);

    $someArray = json_decode($resp, true);

    if (json_last_error() === JSON_ERROR_NONE) {
        $type = $someArray["graphql"]["shortcode_media"]["__typename"];
        $username = $someArray["graphql"]["shortcode_media"]["owner"]["username"];
        $shortcode = $someArray["graphql"]["shortcode_media"]["shortcode"];
        $displayurl = $someArray["graphql"]["shortcode_media"]["display_url"];
        $imagearray = $someArray["graphql"]["shortcode_media"]["display_resources"][2];

        if (isset($someArray["graphql"]["shortcode_media"]["edge_media_to_caption"]["edges"][0])) {
            $caption = $someArray["graphql"]["shortcode_media"]["edge_media_to_caption"]["edges"][0]["node"]["text"];
        } else {
            $caption = "Repost by @instantigapp";
        }
        
        $responce['code'] = "200";
        $responce['status'] = "valid";
        $responce['message'] = "Media";
        $responce['type'] = $type;
        $responce['display_url'] = $displayurl;
        $responce['image_width'] = $imagearray["config_width"];
        $responce['image_height'] = $imagearray["config_height"];
        $responce['media_url'] = $imagearray["src"];
        $responce['username'] = $username;
        $responce['shortcode'] = $shortcode;
        $responce['caption'] = $caption;

        if($type == "GraphVideo") {
            $videourl = $someArray["graphql"]["shortcode_media"]["video_url"];
            $responce['media_url'] = $videourl;
        }

        if($type == "GraphSidecar") {
            $responce['sidecar'] = array();
            $responce['sidecar_type'] = array();
            $sidecar = $someArray["graphql"]["shortcode_media"]["edge_sidecar_to_children"]["edges"];

            for($i=0;$i<count($sidecar);$i++) {

                array_push($responce["sidecar_type"], $sidecar[$i]["node"]["__typename"]);

                if ($sidecar[$i]["node"]["__typename"] == "GraphVideo") {
                    array_push($responce["sidecar"], $sidecar[$i]["node"]["video_url"]);
                } else {
                    array_push($responce["sidecar"], $sidecar[$i]["node"]["display_url"]);
                }       
            }
        }
        
    } else {
        $responce['code'] = "401";
        $responce['status'] = "invalid";
        $responce['message'] = "Private account";
    }
} else {
    $responce['code'] = "401";
    $responce['status'] = "invalid";
    $responce['message'] = "Post url not found";
}

header('Content-Type: application/json');
echo json_encode($responce, JSON_UNESCAPED_SLASHES);
?>