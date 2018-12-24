package stock;

import javax.print.DocFlavor.STRING;

public class ProfitAndLoss {
  private double profit;
  private double loss;
  
  public ProfitAndLoss(double profit, double loss) {
    this.profit = profit;
    this.loss = loss;
  }

  public double getProfit() {
    return profit;
  }

  public void setProfit(double profit) {
    this.profit = profit;
  }

  public double getLoss() {
    return loss;
  }

  public void setLoss(double loss) {
    this.loss = loss;
  }
  
  @Override
  public String toString() {
    return String.format("profit=%f, loss=%f", profit, loss);
  }
}
