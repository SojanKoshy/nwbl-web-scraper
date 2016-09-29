package db;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by sojan on 28/9/16.
 */
//@Entity
//@Table(name = "gerrit_change")
public class GerritChange {
   // @Id
    private Integer id;
    private String street;
    private String city;
    private String province;
    private String country;
    private String postcode;

}
