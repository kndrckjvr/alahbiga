<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Mobile extends CI_Controller {
    
    public function __construct() {
        parent::__construct();
        $this->form_validation->set_error_delimiters('', '');
        // if(!$this->input->post('android', TRUE)) {
        //     show_404();
        // }
    }

    public function register_user() {
        $this->form_validation->set_rules('name', 'Name', 'required|max_length[100]');
        $this->form_validation->set_rules('email', 'Email', 'required|valid_email|is_unique[tbl_user.email]');
        $this->form_validation->set_rules('username', 'Username', 'required|max_length[12]|is_unique[tbl_user.username]');
        $this->form_validation->set_rules('password', 'Password', 'required|min_length[6]');
        $this->form_validation->set_rules('contact', 'Mobile Number', 'required|regex_match[/^(09|\+639)\d{9}$/]', array('regex_match' => '{field} must be a valid Philippine Mobile Number.'));
        $json = array();
        
        if ($this->form_validation->run() == TRUE) {
            $data = array('name' => $this->input->post('name', TRUE),
            'email' => $this->input->post('email', TRUE),
            'username' => $this->input->post('username', TRUE),
            'password' => sha1($this->input->post('password', TRUE)),
            'contact' => $this->input->post('contact', TRUE),
            'verification_code' => random_string('numeric', 6));
            if($this->mobile->insert('tbl_user', $data)) {
                $json['status'] = TRUE;
            }
        } else {
            $json['messages'] = array();
            foreach($_POST as $key => $value){
                if(form_error($key, '', '') != '') {
                    $messages[$key] = form_error($key, '', '');
                }
                $json['status'] = FALSE;
            }
            array_push($json['messages'], $messages);
        }

        echo json_encode($json);
    }

    public function login_user() {
        $this->form_validation->set_rules('username', 'Username', 'required|max_length[12]');
        $this->form_validation->set_rules('password', 'Password', 'required|min_length[6]');
        $json = array();
        if ($this->form_validation->run() == TRUE) {
            $json['messages'] = array();
            $data = array('username' => $this->input->post('username', TRUE),
            'password' => sha1($this->input->post('password', TRUE)));
            if($userdata = $this->mobile->read('tbl_user', array('username' => $data['username']))[0]) {
                if($userdata->password == $data['password']) {
                    if($userdata->status == 1) {
                        $json['status'] = TRUE;
                        $json['database_id'] = $userdata->id;
                    } else if($userdata->status == 0) {
                        $json['status'] = FALSE;
                        array_push($json['messages'], array('error' => 'User not activated!'));
                    } else if($userdata->status == 2) {
                        $json['status'] = FALSE;
                        array_push($json['messages'], array('error' => 'User not yet verified!'));
                    }
                } else {
                    $json['status'] = FALSE;
                    array_push($json['messages'], array('password' => 'Invalid Password!'));
                }
            } else {
                $json['status'] = FALSE;
                $json['messages'] = array('username' => 'Invalid Username!', 'password' => 'Invalid Password!');
            }
        } else {
            $json['messages'] = array();
            foreach($_POST as $key => $value){
                if(form_error($key, '', '') != '') {
                    $messages[$key] = form_error($key, '', '');
                }
                $json['status'] = FALSE;
            }
            array_push($json['messages'], $messages);
        }

        echo json_encode($json);
    }

    public function form_register() {
        $this->load->view('forms/register');
    }

    public function form_login() {
        $this->load->view('forms/login');
    }
}
