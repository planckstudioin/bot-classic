<?php

$responce = array();
$id       = '';
$cookie   = 'ig_did=DD442250-0440-40D2-B5E3-F49970BEDD01; mid=X41DwwALAAHxaxgQAheOIsme4k9H; ig_nrcb=1; csrftoken=5PIoYazrvSH0oHuwfEnqN35LakAb81XJ; ds_user_id=8173227811; sessionid=8173227811%3A3TJBtZDyUjhkye%3A5; shbid=17634; shbts=1603093503.691378; rur=PRN; urlgen="{\"2405:205:c821:8b42:8d77:9f7a:bc5f:2366\": 55836}:1kUPqx:3nqbBg8edyatZYt8c_nLSJdDN1w"';
$header   = 'Mozilla/5.0 (Linux; Android 5.0.1; LG-H342 Build/LRX21Y; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/65.0.3325.109 Mobile Safari/537.36 Instagram 40.0.0.14.95 Android (21/5.0.1; 240dpi; 480x786; LGE/lge; LG-H342; c50ds; c50ds; pt_BR; 102221277)';

if (isset($_REQUEST['username'])) {
    $id = $_REQUEST['username'];
} else {
    $responce['status']  = 'failed';
    $responce['message'] = 'Unknown userid';
    @header('Content-Type: application/json');
    echo json_encode($responce, JSON_UNESCAPED_SLASHES);
    die();
}

$mypost  = array();
$postary = array();

$query_hash = '56a7068fea504063273cc2120ffd54f3';
$first      = '36';
$end_cursor = '';

function getUserId($username)
{
    $curl = curl_init();
    $url  = "https://api.planckstudio.in/bot/v1/curlbasic.php?username=" . $username;

    curl_setopt_array($curl, [
        CURLOPT_RETURNTRANSFER => 1,
        CURLOPT_URL            => $url,
        CURLOPT_HTTPHEADER     => [
            //'ig_did=DD442250-0440-40D2-B5E3-F49970BEDD01; mid=X41DwwALAAHxaxgQAheOIsme4k9H; ig_nrcb=1; csrftoken=5PIoYazrvSH0oHuwfEnqN35LakAb81XJ; ds_user_id=8173227811; sessionid=8173227811%3A3TJBtZDyUjhkye%3A5; shbid=17634; shbts=1603093503.691378; rur=PRN; urlgen="{\"2405:205:c821:8b42:8d77:9f7a:bc5f:2366\": 55836}:1kUPqx:3nqbBg8edyatZYt8c_nLSJdDN1w"'
        ],
        CURLOPT_SSL_VERIFYHOST => 0,
        CURLOPT_FOLLOWLOCATION => false,
        CURLOPT_USERAGENT      => 'Mozilla/5.0 (Linux; Android 5.0.1; LG-H342 Build/LRX21Y; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/65.0.3325.109 Mobile Safari/537.36 Instagram 40.0.0.14.95 Android (21/5.0.1; 240dpi; 480x786; LGE/lge; LG-H342; c50ds; c50ds; pt_BR; 102221277)',
        CURLOPT_HEADER         => false,
    ]);

    $data = curl_exec($curl);
    curl_close($curl);

    $info = json_decode($data, true);
    return $info;
}

function fetchPost($query_hash, $first, $id, $cookie, $header, $after = '')
{
    $postary = array();
    $curl    = curl_init();

    $query_url = 'https://www.instagram.com/graphql/query/?query_hash=' . $query_hash . '&variables=';
    $param_url = '{"id":"' . $id . '","first":' . $first . ',"after":"' . $after . '"}';
    $param_url = str_replace('{', '%7B', $param_url);
    $param_url = str_replace(':', '%3A', $param_url);
    $param_url = str_replace(',', '%2C', $param_url);

    $myurl = $query_url . $param_url;

    curl_setopt_array($curl, [
        CURLOPT_RETURNTRANSFER => 1,
        CURLOPT_URL            => $myurl,
        CURLOPT_HTTPHEADER     => [
            //'ig_did=DD442250-0440-40D2-B5E3-F49970BEDD01; mid=X41DwwALAAHxaxgQAheOIsme4k9H; ig_nrcb=1; csrftoken=5PIoYazrvSH0oHuwfEnqN35LakAb81XJ; ds_user_id=8173227811; sessionid=8173227811%3A3TJBtZDyUjhkye%3A5; shbid=17634; shbts=1603093503.691378; rur=PRN; urlgen="{\"2405:205:c821:8b42:8d77:9f7a:bc5f:2366\": 55836}:1kUPqx:3nqbBg8edyatZYt8c_nLSJdDN1w"'
        ],
        CURLOPT_SSL_VERIFYHOST => 0,
        CURLOPT_FOLLOWLOCATION => false,
        CURLOPT_USERAGENT      => 'Mozilla/5.0 (Linux; Android 5.0.1; LG-H342 Build/LRX21Y; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/65.0.3325.109 Mobile Safari/537.36 Instagram 40.0.0.14.95 Android (21/5.0.1; 240dpi; 480x786; LGE/lge; LG-H342; c50ds; c50ds; pt_BR; 102221277)',
        CURLOPT_HEADER         => false,
    ]);

    $mypostdata = curl_exec($curl);
    curl_close($curl);

    $info = json_decode($mypostdata, true);

    $media      = $info['data']['user']['edge_owner_to_timeline_media'];
    $total      = $media['count'];
    $has_next   = $media['page_info']['has_next_page'];
    $end_cursor = $media['page_info']['end_cursor'];
    $posts      = $media['edges'];

    for ($i = 0; $i < @count($posts); $i++) {
        array_push($postary, $posts[$i]["node"]);
    }

    if ($has_next) {
        $next = fetchPost($query_hash, $first, $id, $cookie, $header, $end_cursor);
        for ($i = 0; $i < count($next); $i++) {
            array_push($postary, $next[$i]);
        }
    }

    return $postary;
}

$basicinfo = getUserId($id);
$posts     = fetchPost($query_hash, $first, $basicinfo["id"], $cookie, $header);

$total_like    = 0;
$total_comment = 0;
$total_view    = 0;

if (count($posts) == 0) {
    $responce['status']  = 'failed';
    $responce['message'] = 'No post found';
    @header('Content-Type: application/json');
    echo json_encode($responce, JSON_UNESCAPED_SLASHES);
    die();
}

$username = $posts[0]["owner"]["username"];
$total    = count($posts);

for ($i = 0; $i < count($posts); $i++) {
    $sidecar = array();

    $type        = $posts[$i]["__typename"];
    @$preview    = $posts[$i]["display_url"];
    @$like       = $posts[$i]["edge_media_preview_like"]["count"];
    @$comment    = $posts[$i]["edge_media_to_comment"]["count"];
    @$code       = $posts[$i]["shortcode"];
    @$id         = $posts[$i]["id"];
    @$caption    = $posts[$i]["edge_media_to_caption"]["edges"][0]["node"]["text"];
    @$dimensions = $posts[$i]["dimensions"];

    if ($type == "GraphVideo") {
        $src  = $posts[$i]["video_url"];
        $view = $posts[$i]["video_view_count"];
    } else {
        $src  = $posts[$i]["display_url"];
        $view = 0;
    }

    if ($type == "GraphSidecar") {
        $car = $posts[$i]["edge_sidecar_to_children"]["edges"];

        for ($s = 0; $s < count($car); $s++) {
            $type       = $car[$s]["node"]["__typename"];
            $dimensions = $car[$s]["node"]["dimensions"];
            if ($type == "GraphVideo") {
                $srcs = $car[$s]["node"]["video_url"];
            } else {
                $srcs = $car[$s]["node"]["display_url"];
            }
            $sc["type"]       = $type;
            $sc["src"]        = $srcs;
            $sc["dimensions"] = $dimensions;
            $sc["hasSidecar"] = false;
            array_push($sidecar, $sc);
        }

        $post["sidecar"]    = $sidecar;
        $post["hasSidecar"] = true;
        $sidecar            = null;
        $car                = null;
        unset($sidecar);
    } else {
        $post["hasSidecar"] = false;
        $post["sidecar"]    = null;
    }

    $post["type"]       = $type;
    $post["preview"]    = $preview;
    $post["src"]        = $src;
    $post["like"]       = $like;
    $post["comment"]    = $comment;
    $post["caption"]    = $caption;
    $post["dimensions"] = $dimensions;
    $post["id"]         = $id;
    $post["code"]       = $code;

    if ($type == 'GraphVideo') {
        $post["view"] = $view;
        $total_view   = $total_view + $view;
    } else {
        $post["view"] = 0;
        $total_view   = $total_view + $view;
    }

    array_push($mypost, $post);

    $total_like    = $total_like + $like;
    $total_comment = $total_comment + $comment;
    $view          = null;
    $type          = null;
}

@$avg_like    = $total_like / $total;
@$avg_comment = $total_comment / $total;
@$avg_view    = $total_view / $total;

$state["totalLikes"]      = $total_like;
$state["totalComments"]   = $total_comment;
$state["totalViews"]      = $total_view;
$state["averageLikes"]    = round($avg_like, 0);
$state["averageComments"] = round($avg_comment, 0);
$state["averageViews"]    = round($avg_view, 0);

if (true) {
    $responce['status']   = 'success';
    $responce['username'] = $username;
    $responce['dp']       = $basicinfo["dp"];
    $responce['userid']   = $basicinfo["id"];
    $responce['total']    = $total;
    $responce['state']    = $state;
    $responce['media']    = $mypost;
} else {
    $responce['status'] = 'failed';
}

@header("Access-Control-Allow-Origin: *");
@header('Content-Type: application/json');
echo json_encode($responce, JSON_UNESCAPED_SLASHES);
