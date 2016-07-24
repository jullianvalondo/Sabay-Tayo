<?php

/***************************************************************
*
* Sabay Tayo (c)
* Created by: Kenneth See, Jullian Valondo, Jett Andres
*
* Receives SMS from the users, processes the contents, then
* replies back to the users also via SMS.
*
***************************************************************/

/***************************************************************
* FUNCTIONS - Begin
***************************************************************/

  // load Wordpress environment to access mySQL underneath
  function find_wordpress_base_path() {
    $dir = dirname(__FILE__);
    do {
      // it is possible to check for other files here
      if( file_exists($dir."/wp-config.php") ) {
        return $dir;
      }
    } while( $dir = realpath("$dir/..") );
      return null;
  }

  // parameter checking of text message
  function isvalid($item, $type) {
    $isvalid = 1;

    // $type can be port, date, time, pax, and notes
    switch ($type) {
      case 'port':
        $isvalid = preg_match("#^[0-9a-zA-Z]+$#", $item);
//        if (! preg_match("#^[0-9a-zA-Z]+$#", $item))
//          $isvalid = 0;
        break;
      case 'date':
        $isvalid = preg_match("#^[0-9]{4}\-((0?[1-9])|(10)|(11)|(12))\-([0-3]?[0-9])$#", $item);
//        if (! preg_match("#^[0-9]{4}\-((0?[1-9])|(10)|(11)|(12))\-([0-3]?[0-9])$#", $item))
//          $isvalid = 0;
        break;
      case 'time':
        $isvalid = preg_match("#^(([0-1]?[0-9])|(20)|(21)|(22)|(23)):([0-5][0-9])$#", $item);
//        if (! preg_match("#^(([0-1]?[0-9])|(20)|(21)|(22)|(23)):([0-5][0-9])$#", $item))
//          $isvalid = 0;
        break;
      case 'pax':
        $isvalid = preg_match("#^[0-9]+$#", $item);
//        if (! preg_match("#^[0-9]+$#", $item))
//          $isvalid = 0;
        break;
    };

//    echo $isvalid;
    return($isvalid);
  }

  function terminate_with_message($errormessage) {
    global $handle;

    fwrite($handle, $errormessage);
    fclose($handle);
    die($errormessage);
  }

  // get access token of the subscriber number
  function get_access_token($phone_number) {
    global $handle,$wpdb;

    $query = "SELECT access_token FROM subscr_acctoken WHERE subscriber_number = '".$phone_number."'" ;
    fwrite($handle, "SQL QUERY: ".$query."\r\n");
    $results = $wpdb->get_results($query);
    $tok = $results[0]->access_token;
    fwrite($handle, "ACCESS TOKEN: ".$tok."\r\n");
    return $tok;
  }

  function send_sms($phone_number, $message) {
    global $handle,$globe;

    $sms = $globe->sms(1666);
    $acctok = get_access_token($phone_number);
    $response = $sms->sendMessage($acctok, $phone_number, $message);
    fwrite($handle, "response: ".$message."\r\n");
  }


/***************************************************************
* FUNCTIONS - End
***************************************************************/

/***************************************************************
* MAIN PROGRAM - Begin
***************************************************************/

// General prep work
  define('BASE_PATH', find_wordpress_base_path()."/");
  define('WP_USE_THEMES', false);
  global $wp, $wp_query, $wp_the_query, $wp_rewrite, $wp_did_header, $wpdb;
  require(BASE_PATH . 'wp-load.php');
  date_default_timezone_set('Asia/Manila');
  $response_sms = 'TY from SABAYTAYO! ';
  // Prep for sending SMS via Globe API
  session_start();
  require ('api/PHP/src/GlobeApi.php');
  $globe = new GlobeApi('v1');

  $log_dir = 'logs';
  // set up log file
  // $my_file = "{$log_dir}/{$timestamp}";
  $my_file = "{$log_dir}/logfile.txt";
  $handle = fopen($my_file, 'a') or die('Cannot open file:  '.$my_file);
  $timestamp = time();
  // get json object which contains phone number and SMS
  $json = file_get_contents('php://input');
  $json = stripslashes($json);
  $jsonvalues = json_decode($json, true);
  // get cell number. NOTE: senderAddr ADDS A "TEL:" STRING TO THE PHONE NUMBER
  $subscriber_number = $jsonvalues[inboundSMSMessageList][inboundSMSMessage][0][senderAddress];
  $subscriber_number = substr($subscriber_number, 4);
//  fwrite($handle, "SUBSCRIBER NUMBER: ".$subscriber_number."\r\n");

// *********** RECEIVE SMS ************
// Get message from SMS
  $text = $jsonvalues[inboundSMSMessageList][inboundSMSMessage][0][message];
  fwrite($handle, "txt message : ".$text."\r\n");

// *********** PROCESS SMS ************
  // START PARSING PARAMETERS
  $parameters = explode("/", $text);

  // filter 1: num of parameters must be 5 (optional notes skipped) or 6
  if (count($parameters) < 5) {
    terminate_with_message($response_sms."Message must follow the following pattern: origin/destination/departure date in YYYY-MM-DD format/latest departure time in HH:mm format, military time/number of passengers/free text notes. Ex 1: sabaytayo/PHPIN/PHSBL/2016-01-31/17:00/3/can leave as early as 14:00");
  }

  // filter 2: parameters must be in the right format
  $port_orig = strtoupper($parameters[0]);
  $port_dest = strtoupper($parameters[1]);
  $dept_date = $parameters[2];
  $dept_time = $parameters[3];
  $pax       = $parameters[4];
  $notes     = $parameters[5];

  if (! isvalid($port_orig, 'port') ) {
    terminate_with_message($response_sms."Origin not in list. Pls refer to list of valid ports.");
  }

  if (! isvalid($port_dest, 'port') ) {
    terminate_with_message($response_sms."Destination not in list. Pls refer to list of valid ports.");
  }

  if (! isvalid($dept_date, 'date') ) {
    terminate_with_message($response_sms."Date format must be YYYY-MM-DD, ex. 2016-01-13 for 13 January 2016");
  }

  if (! isvalid($dept_time, 'time') ) {
    terminate_with_message($response_sms."Time format must be HH:mm, military time, ex. 15:35 for 3:35 PM");
  }

  if (! isvalid($pax, 'pax') ) {
    terminate_with_message($response_sms."Number of passengers must be a whole number.");
  }


  // insert entry into sabaytayo table
  $wpdb->replace('trips', array(
      'subscriber_number' => $subscriber_number
    , 'port_orig' => $port_orig
    , 'port_dest' => $port_dest
    , 'dept_date' => $dept_date
    , 'dept_time' => $dept_time
    , 'pax' => $pax
    , 'notes' => $notes
    , 'timestamp' => $timestamp
    ), array('%s', '%s', '%s', '%s', '%s','%d', '%s', '%s' )
  );
  $output = "Your entry has been added/updated. ";
  // echo $output;
  $response_sms .= $output;

  // prepare query
  $query  = "SELECT * FROM trips ";
//  $query .= "WHERE contact  <> '$contact' ";
  $query .= "WHERE port_orig = '$port_orig' ";
  $query .= "AND   port_dest   = '$port_dest' ";
  $query .= "AND   dept_date  = '$dept_date' ";
  $query .= "AND   STR_TO_DATE(CONCAT(dept_date, ' ', dept_time), '%Y-%m-%d %H:%i:%s')  >= '" . date("Y-m-d H:i:s") . "' ";
//  echo $query;

  // execute query
  $results = $wpdb->get_results($query);
  //  print_r($results);
  if (sizeof($results) > 0) {
    $output = "The ff people are travelling from {$port_orig} to {$port_dest} on {$dept_date}: ";
    echo $output;
    $response_sms .= $output;
    $subscribers = array();
    foreach ( $results as $r ) {
      array_push($subscribers, $r->subscriber_number);
      $output = "$r->subscriber_number (".date('G:i',strtotime($r->dept_time)).", $r->pax pax, $r->notes) ";
      echo $output;
      $response_sms .= $output;
    }
//    reset($subscribers);
    for ($i = 0; $i < sizeof($subscribers); $i++) {
      fwrite($handle, "N: ".$subscribers[$i]."\r\n");
      send_sms($subscribers[$i], $response_sms);
    }
//    foreach ( $subscribers as $n ) {
//      fwrite($handle, "N: ".$n."\r\n");
//      send_sms($n, $response_sms);
//    }
  } else {
    // $output = "You are the first to register travel from {$port_orig} to {$port_dest} on {$dept_date}. ";
    $output = "Currently nobody else is travelling from {$port_orig} to {$port_dest} on {$dept_date}. Your entry will be shown to potential travel companions when they register.";
    echo $output;
    $response_sms .= $output;
    send_sms($subscriber_number, $response_sms);
  };

  fwrite($handle, "OUTPUT: ".$response_sms."\r\n");


// *********** REPLY VIA SMS **********
//  send_sms($subscriber_number, $response_sms);


/***************************************************************
* MAIN PROGRAM - End
***************************************************************/






//$values[inboundSMSMessageList][numberOfMessagesInThisBatch]
//$values[inboundSMSMessageList][inboundSMSMessage][0][message]
//$values[inboundSMSMessageList][inboundSMSMessage][0][senderAddress]


//  fwrite($handle,
//    "Time: ". time()."<br>". "message: ". $values[inboundSMSMessageList][inboundSMSMessage][0][message]."<br>"."Access token: ". $access_token
//  );



  fclose($handle);

?>
