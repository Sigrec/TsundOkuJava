package TsundOkuApp.PriceAnalysis;

import javafx.beans.property.SimpleStringProperty;

public class PriceComparisonDataModel {
	SimpleStringProperty title;
	SimpleStringProperty price;
	SimpleStringProperty stockStatus;
	SimpleStringProperty website;

	public PriceComparisonDataModel(SimpleStringProperty title, SimpleStringProperty price, SimpleStringProperty stockStatus, SimpleStringProperty website) {
		this.title = title;
		this.price = price;
		this.stockStatus = stockStatus;
		this.website = website;
	}

	public String getTitle() {
		return title.get();
	}

	public SimpleStringProperty titleProperty() {
		return title;
	}

	public void setTitle(String title) {
		this.title.set(title);
	}

	public String getPrice() {
		return price.get();
	}

	public SimpleStringProperty priceProperty() {
		return price;
	}

	public void setPrice(String price) {
		this.price.set(price);
	}

	public String getStockStatus() {
		return stockStatus.get();
	}

	public SimpleStringProperty stockStatusProperty() {
		return stockStatus;
	}

	public void setStockStatus(String stockStatus) {
		this.stockStatus.set(stockStatus);
	}

	public String getWebsite() {
		return website.get();
	}

	public SimpleStringProperty websiteProperty() {
		return website;
	}

	public void setWebsite(String website) {
		this.website.set(website);
	}
}
