import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;


public class WordSet {
    // wrappers of all words
    private List<WordWrapper> words = new LinkedList<WordWrapper>();

    // groups to which belongs those words (one wordWrapper can belong only to one group)
    private List<Group> groups = new ArrayList<Group>();
    
    public WordSet(List<String> words) {
        //making word wrapper and group for each word
        for(String word : words) {
            WordWrapper crntWordWrapper = new WordWrapper(word); 
            this.words.add(crntWordWrapper);
            Group crntGroup = new Group(crntWordWrapper);
            this.groups.add(crntGroup);
        }
    }
    
    public List<Group> getGroups() {
        return groups;
    }
    
    public void stemAll() throws Exception {
        for (WordWrapper wrd : words) {
            wrd.findStemmedForms();
        }
    }
    
    public void uniteGroups() {
        boolean wasSomeGroupUnitedInLastCycle = true;
        while (wasSomeGroupUnitedInLastCycle) {
            wasSomeGroupUnitedInLastCycle = false;
            for (ListIterator<Group> frstIter = groups.listIterator(); frstIter.hasNext();) {
                Group frstGrp = frstIter.next();
                if (frstGrp.isMarkedForDeletion()) {
                    frstIter.remove();
                    continue;
                }
                for (ListIterator<Group> scndIter = groups.listIterator(frstIter.nextIndex()); scndIter.hasNext();) {
                    Group scndGrp = scndIter.next();
                    if (frstGrp.hasIntersactionByRootPartsOfWords(scndGrp)) {
                        frstGrp.getElements().addAll(scndGrp.getElements());
                        scndGrp.setMarkedForDeletion(true);
                        wasSomeGroupUnitedInLastCycle = true;
                    }
                }
            }
        }
    }
}
