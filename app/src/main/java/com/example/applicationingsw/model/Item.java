package com.example.applicationingsw.model;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Item {

    private Integer id;
    private String name;
    private String manufacturer;
    private String price;
    private String description;
    private Integer quantity;
    private List<String> tags;
    private String category;
    private String url;
    private boolean isLoading = false;
    private boolean isNew = true;



    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getManufacturer() {
        return manufacturer;
    }
    public String getPrice() { return price;   }
    public String getDescription() {
        return description;
    }
    public String getUrl() {return url; }
    public int getQuantity() {
        return quantity;
    }
    public List<String> getTags(){return tags;}
    public String getCategory(){return category;}
    public void setId(int id) {
        this.id = (id);
    }
    public void setName(String name) { this.name = (name); }
    public void setManufacturer(String manufacturer) {  this.manufacturer = (manufacturer); }
    public void setPrice(float price) {    this.price = price + "€"; }
    public void setDescription(String description) {    this.description = (description);    }
    public void setQuantity(int quantity) {
        this.quantity = (quantity);
    }
    public void setTags (List<String> tags){ this.tags = (tags); }
    public void setCategory ( String category){ this.category = (category); }
    public void setUrl (String url){
        this.url = url;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public Item() {}

    public Item(int anId, String aName, String aManufacturer, float aPrice, String aDescription, int aQuantity,String anUrl,String aCategory,List<String>tagsList) {
        id = (anId);
        name =  (aName);
        price = aPrice + "€";
        manufacturer =  (aManufacturer);
        description =  (aDescription);
        quantity = (aQuantity);
        url = (anUrl);
        tags = tagsList;
        category = aCategory;
    }

    public Item( String aName, String aManufacturer, float aPrice, String aDescription, int aQuantity,String anUrl,String aCategory) {
        name =  (aName);
        price = aPrice + "€";
        manufacturer =  (aManufacturer);
        description =  (aDescription);
        quantity = (aQuantity);
        url = (anUrl);
        tags = new ArrayList<>();
        category = aCategory;
    }

    @Override
    final public boolean equals(Object o){
        if(!(o instanceof Item)){
            return false;
        }
        else {
            Item curr = (Item) o;
            if(curr.getId() == this.getId()){
                return true;
            }
            else {
                return false;
            }
        }
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    @Override
    public String toString(){
        return "Nome: " + getName();
    }
}