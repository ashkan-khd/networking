public class Score {
    private Account account;
    private int number;

    public Score(Account account, int number) {
        this.account = account;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void addNumber(int number) {
        this.number += number;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Account && ((Account) obj).equals(this.account);
    }

    @Override
    public String toString() {
        return "Username : " + account.getUsername() + "\nNumber : " + number;
    }

}
