package com.example.applicationingsw.model;


import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.applicationingsw.App;
import com.example.applicationingsw.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Item implements Parcelable {

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

    protected Item(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        manufacturer = in.readString();
        price = in.readString();
        description = in.readString();
        if (in.readByte() == 0) {
            quantity = null;
        } else {
            quantity = in.readInt();
        }
        tags = in.createStringArrayList();
        category = in.readString();
        url = in.readString();
        isLoading = in.readByte() != 0;
        isNew = in.readByte() != 0;
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getManufacturer() {
        return manufacturer;
    }
    public String getPriceWithConcurrency() { return price;   }
    public float getPriceWithoutConcurrency(){return Float.valueOf(price.replaceAll(App.getAppContext().getString(R.string.concurrency),""));}
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
    public void setPrice(float price) {    this.price = price + Resources.getSystem().getString(R.string.concurrency); }
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
        price = aPrice + App.getAppContext().getResources().getString(R.string.concurrency);
        manufacturer =  (aManufacturer);
        description =  (aDescription);
        quantity = (aQuantity);
        url = (anUrl);
        tags = tagsList;
        category = aCategory;
    }

    public Item( String aName, String aManufacturer, float aPrice, String aDescription, int aQuantity,String anUrl,String aCategory) {
        name =  (aName);
        price = aPrice + Resources.getSystem().getString(R.string.concurrency);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        parcel.writeString(name);
        parcel.writeString(manufacturer);
        parcel.writeString(price);
        parcel.writeString(description);
        if (quantity == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(quantity);
        }
        parcel.writeStringList(tags);
        parcel.writeString(category);
        parcel.writeString(url);
        parcel.writeByte((byte) (isLoading ? 1 : 0));
        parcel.writeByte((byte) (isNew ? 1 : 0));
    }
}