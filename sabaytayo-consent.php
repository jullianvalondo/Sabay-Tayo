<?php

/***************************************************************
*
* Sabay Tayo (c)
* Created by: Kenneth See, Jullian Valondo, Jett Andres
*
***************************************************************/

  // load Wordpress environment
  function find_wordpress_base_path() {
    $dir = dirname(__FILE__);
    do {
      //it is possible to check for other files here
      if( file_exists($dir."/wp-config.php") ) {
        return $dir;
      }
    } while( $dir = realpath("$dir/..") );
      return null;
  }

  define('BASE_PATH', find_wordpress_base_path()."/");
  define('WP_USE_THEMES', false);
  global $wp, $wp_query, $wp_the_query, $wp_rewrite, $wp_did_header, $wpdb;
  require(BASE_PATH . 'wp-load.php');
  date_default_timezone_set('Asia/Manila');

  $access_token      = $_GET["access_token"];
  $subscriber_number = "+63".$_GET["subscriber_number"];

  // save access token and subscriber number in database
  $wpdb->replace('subscr_acctoken', array(
      'subscriber_number' => $subscriber_number
    , 'access_token' => $access_token
    ), array('%s', '%s')
  );

  // sanity check
  $handle = fopen('consent.txt', 'w') or die('Cannot open file:  '.'consent.txt');
  fwrite($handle, "Access Token: ".$access_token." Subscriber Number: ".$subscriber_number);
  fclose($handle);

?>

