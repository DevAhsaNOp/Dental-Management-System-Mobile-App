package com.csharpui.myloginform;

public class Doctor {
    private String ID,imageUrl,fname,lname,aboutme,phone,email;
    String Specializtion,YearOfExperience;
    public Doctor(String ID,String imageUrl, String fname, String lname, String aboutme, String Specializtion,
                  String YearOfExperience,String phone,String email) {
        this.ID = ID;
        this.imageUrl = imageUrl;
        this.fname = fname;
        this.lname = lname;
        this.aboutme = aboutme;
        this.Specializtion = Specializtion;
        this.YearOfExperience = YearOfExperience;
        this.phone = phone;
        this.email = email;
    }
    public String getID(){return ID;}
    public String getPhone(){return phone;}

    public String getEmail(){return email;}

    public String getImageUrl() {
        return imageUrl = imageUrl.replace("~", "https://dmswebappofficial.azurewebsites.net");
    }

    public String getName() {
        return fname.concat(" "+lname);
    }

    public String getAboutme() {
        return aboutme;
    }
    public String getSpecializtion() {
        return Specializtion;
    }
    public String getYearOfExperience() {
        return YearOfExperience;
    }


}
