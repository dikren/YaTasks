import java.util.LinkedList;
import java.util.List;

public class Group {

    // elements of this group
    private List<WordWrapper> elements = new LinkedList<WordWrapper>();

    // this is used to determine if current group already was emptied into another group.
    private boolean markedForDeletion = false;

    public Group(List<WordWrapper> words) {
        elements.addAll(words);
    }

    public Group(WordWrapper word) {
        elements.add(word);
    }

    public List<WordWrapper> getElements() {
        return elements;
    }

    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    public void setMarkedForDeletion(boolean markedForDeletion) {
        this.markedForDeletion = markedForDeletion;
    }
    
    public boolean hasIntersactionByRootPartsOfWords(Group otherGrp) {
        for (WordWrapper frstWrd : this.elements) {
            for (WordWrapper scndWrd : otherGrp.elements) {
                if (frstWrd.hasSameRootAs(scndWrd)) {
                    return true;
                }
            }
        }
        return false;
    }
}
