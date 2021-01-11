<?php

header("Access-Control-Allow-Origin: *");

if (isset($_REQUEST["username"])) {

    $link = $_REQUEST["username"];

    $curl = curl_init();

    curl_setopt_array($curl, [

        CURLOPT_RETURNTRANSFER => 1,

        CURLOPT_URL            => 'https://www.instagram.com/' . $link . '/?__a=1',

        CURLOPT_HTTPHEADER     => [

            'Cookie: ig_did=DD442250-0440-40D2-B5E3-F49970BEDD01; mid=X41DwwALAAHxaxgQAheOIsme4k9H; ig_nrcb=1; csrftoken=5PIoYazrvSH0oHuwfEnqN35LakAb81XJ; ds_user_id=8173227811; sessionid=8173227811%3A3TJBtZDyUjhkye%3A5; shbid=17634; shbts=1603093503.691378; rur=PRN; urlgen="{\"2405:205:c821:8b42:8d77:9f7a:bc5f:2366\": 55836}:1kUPqx:3nqbBg8edyatZYt8c_nLSJdDN1w"',

        ],

        CURLOPT_SSL_VERIFYHOST => 0,

        CURLOPT_FOLLOWLOCATION => false,

        CURLOPT_USERAGENT      => 'Mozilla/5.0 (Linux; Android 6.0.1; Moto G (4)) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Mobile Safari/537.36',

        CURLOPT_HEADER         => false,

    ]);

    $resp = curl_exec($curl);

    curl_close($curl);

    $someArray = json_decode($resp, true);

    if (json_last_error() === JSON_ERROR_NONE) {

        $image = $someArray["graphql"]["user"]["profile_pic_url_hd"];

        $responce['code'] = "200";

        $responce['status'] = "true";

        $responce['message'] = "success";

        $responce['media_url'] = $image;

        $responce['username'] = $link;

        $responce['id'] = $someArray['graphql']['user']['id'];

        $responce['name'] = $someArray['graphql']['user']['full_name'];

        $responce['dp'] = $image;

        $responce['followers'] = $someArray['graphql']['user']['edge_followed_by']['count'];

        $responce['following'] = $someArray['graphql']['user']['edge_follow']['count'];

    } else {

        $responce['status'] = "401";

        $responce['message'] = "Invalid";

        $responce['message'] = "Something goes wrong";

    }

} else {
    $responce['code']    = "401";
    $responce['status']  = "false";
    $responce['message'] = "Invalid username";
}

header('Content-Type: application/json');
echo json_encode($responce, JSON_UNESCAPED_SLASHES);
