<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class MobileModel extends CI_Model {
    
    public function __construct() {
        parent::__construct();
    }

    public function insert($table, $data, $where = NULL) {
        if(!empty($where))
            $this->db->where($where);
        $this->db->insert($table, $data);
        return $this->db->affected_rows();
    }

    public function read($table, $where = NULL) {
        if(!empty($where))
            $this->db->where($where);
        $query = $this->db->get($table);
        return ($query->num_rows() > 0) ? $query->result() : FALSE;
    }
}