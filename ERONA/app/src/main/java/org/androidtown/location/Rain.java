package org.androidtown.location;

public class Rain {

    public boolean SearchIsRainy(int weatherId) {
        boolean isRainy = false;

        switch (weatherId/100) {
            case 2:
                isRainy = true;
                break;
            case 3:
                isRainy = true;
                break;
            case 5:
                isRainy = true;
                break;
            case 6:
                isRainy = true;
                break;
            default:
                isRainy = false;
                break;
        }

        switch (weatherId) {
            case 900:
                isRainy = true;
                break;
            case 901:
                isRainy = true;
                break;
            case 902:
                isRainy = true;
                break;
            case 906:
                isRainy = true;
                break;
            case 960:
                isRainy = true;
                break;
            case 961:
                isRainy = true;
                break;
            case 962:
                isRainy = true;
                break;
        }

        return isRainy;
    }
}
